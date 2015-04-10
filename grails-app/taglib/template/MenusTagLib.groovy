package template

class MenusTagLib {
    //static defaultEncodeAs = [taglib:'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]
    
    def adminMenu = { attrs, body ->
       def output="<aside id='rightSide' class='admin'>"+
                            "<p><strong>Administration Links</strong></p>"+
                            "<nav>" +
                                "<ul>"+
                                    "<li><a href='${g.createLink(controller:"admin",action:"index")}'>Administration Home</a></li>"+
                                    "<li><a href='${g.createLink(controller:"admin",action:"administrators")}'>Application Administrators</a>"
                                "</ul>"+
                            "</nav>"+
                        "</aside>"
       
       out << body() << output
                                
    }
    
}
