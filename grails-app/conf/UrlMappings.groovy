class UrlMappings {

	static mappings = {
            //Standard mappings
            "/$controller/$action?/$id?(.$format)?"{
                constraints {
                    // apply constraints here
                }
            }

            //Homepage
            "/"(controller:"Index",action:"index")

            //Commmon Error pages
            "500"(view:'/error')
            "404"(view:'/404')
            "401"(view:"/401")	
        }
}
