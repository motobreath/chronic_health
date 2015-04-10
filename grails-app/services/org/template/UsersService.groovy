package org.template


class UsersService {
    
    //static transactional = false

    def getAdministrators() {
        /*
        def c=User.createCriteria()
        return c.list{
            roles{
                eq("Administrator")
            }
        }
       */
        return User.withCriteria{
            roles{
                eq("name","Administrator")
            }
        }
       /*
       def users = User.list()
       return users.findAll {it.roles.name="Administrator"}
       */
    }
}
    