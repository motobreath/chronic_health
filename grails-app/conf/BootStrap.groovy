
import org.template.Role
import org.template.User

class BootStrap {

    def init = { servletContext ->
        //Add Role:Administrator
        def roleCheck=Role.findByName("Administrator")
        if(!roleCheck){
            def adminRole = new Role(name: "Administrator")
            adminRole.addToPermissions("admin:*")       
            adminRole.save()
        }
        //Add Role:Guest
        def guestRoleCheck=Role.findByName("Guest")
        if(!guestRoleCheck){
            def guestRole = new Role(name:"Guest")
            guestRole.save()
        }
        
        //TODO: Add default administrator user
    }

    def destroy = {
    }
        
}