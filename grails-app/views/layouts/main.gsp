<!doctype html>
<!--[if lt IE 7]> <html class="no-js lt-ie9 lt-ie8 lt-ie7" lang="en"> <![endif]-->
<!--[if IE 7]>    <html class="no-js lt-ie9 lt-ie8" lang="en"> <![endif]-->
<!--[if IE 8]>    <html class="no-js lt-ie9" lang="en"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js" lang="en"> <!--<![endif]-->
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title><g:layoutTitle default="Template"/></title>
        <meta name="description" content="">
        <meta name="viewport" content="width=device-width">    
        <link rel="shortcut icon" href="${assetPath(src: 'favicon.ico')}" type="image/x-icon">
        <!--
        <link rel="apple-touch-icon" href="${assetPath(src: 'apple-touch-icon.png')}">
        <link rel="apple-touch-icon" sizes="114x114" href="${assetPath(src: 'apple-touch-icon-retina.png')}">
        -->
        <asset:stylesheet src="styles.css"/>
        <asset:stylesheet src="mobile.css"/>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
        <asset:javascript src="lib/jquery-1.11.2.min.js"/>
        <asset:javascript src="lib/angular.js"/>
        <g:layoutHead/>
    </head>

    <body>
        <div id="container">
            <header>
                <a href="${createLink(controller:"index",action:"index")}" id="homeLink">
                    <asset:image src="logo.png" alt="UC Merced"/>
                </a>
                <span>Template</span>
                <nav id="smallNav" class="top">
                    <shiro:isNotLoggedIn>
                        <ul>
                            <li>Welcome Guest</li>
                            <li><a href="${createLink(controller:"index",action:"index")}">Home</a></li>
                            <li>|</li>
                            <li><a href="${createLink(controller:"auth",action:"login")}">Login</a></li>
                        </ul>
                    </shiro:isNotLoggedIn>
                    <shiro:isLoggedIn>
                        <ul>
                            <li>Welcome ${current_user}</li>
                            <li><a href="${createLink(controller:"index",action:"index")}">Home</a></li>
                            <shiro:hasRole name="Administrator">
                                <li>|</li>
                                <li><a href="${createLink(controller:"admin",action:"index")}">Administration</a></li>
                            </shiro:hasRole>
                            <li>|</li>
                            <li><a href="${createLink(controller:"auth",action:"logout")}">Logout</a></li>
                        </ul>
                    </shiro:isLoggedIn>
                </nav>
                <nav>
                    <ul>
                        <li><a href="#">Link 1</a></li>
                    </ul>
                </nav>
            </header>
            <div id="main" role="main">                
                <g:layoutBody/>
            </div>
            <footer>
                <div class="left">
                    <span>Resources</span>
                    <ul>
                        <li><a href="http://www.ucmerced.edu/site-list">Site List</a></li>
                        <li><a href="http://directory.ucmerced.edu" target="_blank">Directory</a></li>
                        <li><a href="http://www.ucmerced.edu/directions-map">Directions &amp; Map</a></li>
                        <li><a href="http://www.ucmerced.edu/about-uc-merced">About UC Merced</a></li>
                        <li><a href="mailto:communications@ucmerced.edu?subject=Link Shortener">Site Contact</a></li>
                    </ul>

                </div>

                <div class="right">
                    <asset:image src="ftrLogo.gif" alt="UC Merced"/>
                    <br />
                    The first new American research university of the 21<sup>st</sup> century, with a mission of research, teaching, and service
                </div>
                <div class="ftr">
                    <p class="pLeft"><strong>University of California</strong>, Merced 5200 Lake Rd, Merced, CA 95344 T:(209) 228-4400</p>
                    <p class="pRight">&COPY; <g:formatDate format="yyyy" date="${new Date()}"/> UC Regents | <a href="http://www.ucmerced.edu/privacy-statement" target="_blank">Privacy/Legal Notice</a>
                </div>
            </footer>
        </div> <!-- eo #container -->
        <asset:javascript src="application.js" />   

        <script>
          /*
          var _gaq=[['_setAccount','UA-XXXXX-X'],['_trackPageview']];
          (function(d,t){var g=d.createElement(t),s=d.getElementsByTagName(t)[0];
          g.src=('https:'==location.protocol?'//ssl':'//www')+'.google-analytics.com/ga.js';
          s.parentNode.insertBefore(g,s)}(document,'script'));
          */
        </script>
        
    </body>
</html>