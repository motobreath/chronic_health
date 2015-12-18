// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

grails.config.locations = ["file:grails-app/conf/localConfig.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination

// The ACCEPT header will not be used for content negotiation for user agents containing the following strings (defaults to the 4 major rendering engines)
grails.mime.disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
grails.mime.types = [ // the first one is the default format
    all:           '*/*', // 'all' maps to '*' or the first available format in withFormat
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    hal:           ['application/hal+json','application/hal+xml'],
    xml:           ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "html"

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'singleton'

// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside ${}
                scriptlet = 'html' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        // filteringCodecForContentType.'text/html' = 'html'
    }
}


grails.converters.encoding = "UTF-8"
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

// configure passing transaction's read-only attribute to Hibernate session, queries and criterias
// set "singleSession = false" OSIV mode in hibernate configuration after enabling
grails.hibernate.pass.readonly = false
// configure passing read-only to OSIV session by default, requires "singleSession = false" OSIV mode
grails.hibernate.osiv.readonly = false

environments {
    development {
        grails.logging.jul.usebridge = true
        grails.serverURL=""
    }
    test{
        grails.serverURL=""
    }
    production {
        grails.logging.jul.usebridge = false
        // TODO: grails.serverURL = "http://www.changeme.com"
    }
}

// log4j configuration
log4j.main = {
    root {
        
    }
    // Example of changing the log pattern for the default console appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}
       
    
    error  'org.codehaus.groovy.grails.web.servlet',        // controllers
           'org.codehaus.groovy.grails.web.pages',          // GSP
           'org.codehaus.groovy.grails.web.sitemesh',       // layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping',        // URL mapping
           'org.codehaus.groovy.grails.commons',            // core / classloading
           'org.codehaus.groovy.grails.plugins',            // plugins
           'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
    
    all "grails.app"
        
    
}

//CAS Information

//Shiro CAS
/***********************************/
//security.shiro.cas.serverUrl (REQUIRED): The URL of the CAS instance to authenticate against. This should be an HTTPS URL.
security.shiro.cas.serverUrl="https://cas.ucmerced.edu/cas"

//security.shiro.cas.baseServiceUrl (OPTIONAL): The base URL to pass to CAS as the service parameter (see CAS Protocol for more details on how this is used). 
//This should be the protocol/host/port portion of your application server's URL with the application context. 
//If unset, the plugin will attempt to determine the base URL dynamically.
//security.shiro.cas.baseServiceUrl=""

//security.shiro.cas.servicePath (OPTIONAL): This should be the path at which end-users can reach the /shiro-cas path within the current application
//(which is automatically registered by this plugin). Defaults to /shiro-cas.
//security.shiro.cas.servicePath="/"

//security.shiro.cas.failurePath (OPTIONAL): The path that users are redirected to if ticket validation fails. Defaults to /auth/cas-failure.
security.shiro.cas.failurePath="/unauthorized"

//security.shiro.cas.loginUrl (OPTIONAL): The URL that users are redirected to when login is required. By default, this directs users to /login within the serverUrl, 
//passing along the service as a query parameter.

//
//security.shiro.cas.logoutUrl (OPTIONAL): The URL that users are redirected to when logging out. By default, this directs users to /logout within the serverUrl, 
//passing along the service as a query parameter.
//
//security.shiro.cas.loginParameters (OPTIONAL): Key, Value pairs to be added to the generated or explicitly set loginUrl. (see CAS Parameters for more details on how this is used)
//
//security.shiro.cas.singleSignOut.disabled (OPTIONAL): Boolean value controlling whether to disable Single Sign Out. 
//By default, this is false, resulting in Single Sign Out support being enabled (matching the default for CAS). 
//Note that this configuration value is used at build-time to modify the web.xml, and externalized configuration will not be taken into account during that phase.
security.shiro.cas.singleSignOut.disabled=true

//security.shiro.cas.singleSignOut.artifactParameterName (OPTIONAL): The parameter used to detect sessions in preparation for Single Sign Out support. By default, this is ticket (matching the default for CAS).
//
//security.shiro.cas.singleSignOut.logoutParameterName (OPTIONAL): The parameter used to detect logout requests. By default, this is logoutRequest (matching the default for CAS).

/************************************/




