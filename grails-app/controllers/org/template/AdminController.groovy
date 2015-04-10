package org.template


import org.springframework.dao.DataIntegrityViolationException

import org.template.User
import org.template.Role

class AdminController {
    
    def usersService
    
    def index() {
        
    }
    
    def administrators(){
        if(params["ucmnetid"]){
            log.info("saving user ${params["ucmnetid"]}")
            User.withTransaction{
                def user=User.findByUsername(params["ucmnetid"])
                Role role = Role.findByName("Administrator")
                if(!user){
                    user=new User(
                        username: params["ucmnetid"]
                    )
                    if(!user.save()){
                        flash.error="Error adding administrator, please try again"
                        redirect(action: "administrators")
                    }
                    user.addToRoles(role)
                    flash.message = "Success! User Added"
                    
                }
                else{
                    user.addToRoles(role)
                    flash.message = "Success! User Added"
                }
            }   
        }
        if(params["id"]){
            def user = User.get(params["id"])
            if(!user){
                redirect(action: "administrators")
            }
            try {                
                user.delete(flush: true)
                flash.message="Success! The user has been deleted";
                redirect(action:"administrators")
            }
            catch (org.springframework.dao.DataIntegrityViolationException e) {
                flash.error = "Could not delete person ${p.name}"
                redirect(action: "administrators")
            }
            catch(java.lang.NullPointerException e){
                flash.error = "Could not delete that person, already deleted?"
                redirect(action:"administrators")
            }
        }
        
        //Return Params
        [users:usersService.getAdministrators()]
    }
}
