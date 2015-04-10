package org.template

import org.apache.shiro.authc.AccountException
import org.apache.shiro.authc.SimpleAuthenticationInfo
import org.apache.shiro.cas.CasAuthenticationException
import org.apache.shiro.cas.CasToken
import org.apache.shiro.cas.grails.ShiroCasConfigUtils
import org.apache.shiro.cas.grails.ShiroCasPrincipalManager
import org.apache.shiro.subject.PrincipalCollection
import org.apache.shiro.subject.SimplePrincipalCollection
import org.apache.shiro.util.CollectionUtils
import org.jasig.cas.client.authentication.AttributePrincipal
import org.jasig.cas.client.validation.TicketValidationException
import org.jasig.cas.client.validation.TicketValidator
import org.template.User
import org.template.Role

/**
 * Simple realm that authenticates users against an CAS server.
 */
class ShiroCasRealm{
    static authTokenClass = CasToken

    TicketValidator casTicketValidator
    def shiroPermissionResolver

    def authenticate(CasToken authToken) {
        def ticket = preValidate(authToken)
        log.info("Attempting to validate ticket ${ticket} against CAS...")
        
        try {
            def casAssertion = casTicketValidator.validate(ticket, ShiroCasConfigUtils.validationUrl)
            def casPrincipal = casAssertion.principal
            def username = casPrincipal.name
            if (log.infoEnabled) {
                log.info("Validated ticket ${ticket} as ${username}")
                log.info("With attributes: ${casPrincipal.attributes}")
            }
            updateAuthToken(authToken, casPrincipal)
            createApplicationPrincipal(casPrincipal)
            List<Object> principals = CollectionUtils.asList(username, casPrincipal.attributes)
            PrincipalCollection principalCollection = new SimplePrincipalCollection(principals, this.toString())
            return new SimpleAuthenticationInfo(principalCollection, ticket)
        } catch (TicketValidationException ex) {
            log.error("Unable to validate ticket ${ticket}", ex)
            throw new CasAuthenticationException("Unable to validate ticket ${ticket}", ex)
        }
    }

    private Object createApplicationPrincipal(AttributePrincipal casPrincipal) {
        
        User.withTransaction{
            def user=new User(
            username: casPrincipal.name,
            givenName: casPrincipal.attributes.givenName,
            sn: casPrincipal.attributes.sn
            )
            def p=User.findByUsername(casPrincipal.name)
            if(!p){
                log.info "Saving user ${casPrincipal.name}"     
                Role role = Role.findByName("Guest")
                user.addToRoles(role)
                if(!user.save()){
                    log.error "could not save:" << user.errors.allErrors
                    throw new CasAuthenticationException("Unable to save user ${user.errors.allErrors}")
                }
                
                p=user
            }
            else{
                p.username=casPrincipal.name
                p.givenName=casPrincipal.attributes.givenName
                p.sn=casPrincipal.attributes.sn
                p.save()
                log.info "User ${casPrincipal.name} already exists, updated"
            }
        }                  
        
        
        
    }

    private static String preValidate(CasToken authToken) {
        def ticket = authToken?.credentials as String
        if (!ticket) {
            throw new AccountException("No ticket found")
        }
        return ticket
    }

    private static void updateAuthToken(CasToken authToken, AttributePrincipal casPrincipal) {
        def attributes = casPrincipal.attributes
        authToken.setUserId(casPrincipal.name)
        def isRemembered = (attributes["longTermAuthenticationRequestTokenUsed"] as String)?.toBoolean()
        if (isRemembered) {
            authToken.setRememberMe(true)
        }
        ShiroCasPrincipalManager.rememberPrincipalForToken(authToken)
    }
    def hasRole(principal, roleName) {
        def roles = User.withCriteria {
            roles {
                eq("name", roleName)
            }
            eq("username", principal)
        }
 
        return roles.size() > 0
    }
 
    def hasAllRoles(principal, roles) {
        def r = User.withCriteria {
            roles {
                'in'("name", roles)
            }
            eq("username", principal)
        }
 
        return r.size() == roles.size()
    }
 
    def isPermitted(principal, requiredPermission) {
        // Does the user have the given permission directly associated
        // with himself?
        //
        // First find all the permissions that the user has that match
        // the required permission's type and project code.
        def user = User.findByUsername(principal, [cache: true])
        if (user == null) return false
 
        def permissions = user.permissions
 
        // Try each of the permissions found and see whether any of
        // them confer the required permission.
        def retval = permissions?.find { permString ->
            // Create a real permission instance from the database
            // permission.
            def perm = shiroPermissionResolver.resolvePermission(permString)
 
            // Now check whether this permission implies the required
            // one.
            if (perm.implies(requiredPermission)) {
                // User has the permission!
                return true
            }
            else {
                return false
            }
        }
 
        if (retval != null) {
            // Found a matching permission!
            return true
        }
 
        // If not, does he gain it through a role?
        //
        // Get the permissions from the roles that the user does have.
        def results = User.executeQuery("select distinct p from User as user join user.roles as role join role.permissions as p where user.username = ?", [principal], [cache: true])
 
        // There may be some duplicate entries in the results, but
        // at this stage it is not worth trying to remove them. Now,
        // create a real permission from each result and check it
        // against the required one.
        retval = results.find { permString ->
            // Create a real permission instance from the database
            // permission.
            def perm = shiroPermissionResolver.resolvePermission(permString)
 
            // Now check whether this permission implies the required
            // one.
            if (perm.implies(requiredPermission)) {
                // User has the permission!
                return true
            }
            else {
                return false
            }
        }
 
        if (retval != null) {
            // Found a matching permission!
            return true
        }
        else {
            return false
        }
    }
}
