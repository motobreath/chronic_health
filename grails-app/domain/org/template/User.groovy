package org.template

class User {
    String username
    String givenName
    String sn
    
    String getFullName() { givenName + " " + sn}
    
    static hasMany = [ roles: Role, permissions: String ]
    
    static transients = ['getFullName']
    
    static constraints = {
        username(nullable: false, blank: false, unique: true)
        givenName(nullable:true, blank:true)
        sn(nullable:true,blank:true)
        //givenName and sn nullable: false by default
    }
    
}
