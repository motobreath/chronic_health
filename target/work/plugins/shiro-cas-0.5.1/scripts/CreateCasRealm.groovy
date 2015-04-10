/*
 * Copyright 2007 Peter Ledbrook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 *
 * Modified 2009 Bradley Beddoes, Intient Pty Ltd, Ported to Apache Ki
 * Modified 2009 Kapil Sachdeva, Gemalto Inc, Ported to Apache Shiro
 */

includeTargets << grailsScript("_GrailsArgParsing")

USAGE = """
    create-cas-realm [--prefix=PREFIX]

where
    PREFIX = The prefix to add to the name of the realm. This may include a
             package. (default: "Shiro").
"""

target(createCasRealm: "Creates a new CAS realm.") {
    // Make sure any arguments have been parsed.
    depends(parseArguments)

    def (pkg, prefix) = parsePrefix()

    // Copy over the template LDAP realm.
    def className = "${prefix}CasRealm"
    installTemplateEx("${className}.groovy", "grails-app/realms${packageToPath(pkg)}", "realms", "ShiroCasRealm.groovy") {
        ant.replace(file: artefactFile) {
            ant.replacefilter(token: "@package.line@", value: (pkg ? "package ${pkg}\n\n" : ""))
            ant.replacefilter(token: '@realm.name@', value: className)
        }
    }

    event("CreatedArtefact", ['Realm', className])
}

private parsePrefix() {
    def prefix = "Shiro"
    def pkg = ""
    if (argsMap["prefix"] != null) {
        def givenValue = argsMap["prefix"].split(/\./, -1)
        prefix = givenValue[-1]
        pkg = givenValue.size() > 1 ? givenValue[0..-2].join('.') : ""
    }

    return [ pkg, prefix ]
}

/**
 * Converts a package name (with '.' separators) to a file path (with
 * '/' separators). If the package is <tt>null</tt>, this returns an
 * empty string.
 */
private packageToPath(String pkg) {
    return pkg ? '/' + pkg.replace('.' as char, '/' as char) : ''
}

/**
 * Installs a Shiro template into the current project, with optional
 * post-processing.
 * @param artefactName The name of the file to create.
 * @param artefactPath The location relative to the project root to
 * store the new file.
 * @param templatePath The location relative to the plugin's
 * "src/templates" directory where the template file can be found.
 * @param templateName The filename of the template.
 * @param c An optional closure that will be invoked once the template
 * has been installed. The closure has the property "artefactFile"
 * available to it, which is the file path (as a File) of the newly
 * created file. This parameter may be <code>null</code>, in which
 * no post-processing is performed.
 */
installTemplateEx = { String artefactName, String artefactPath, String templatePath, String templateName, Closure c ->
    // Copy over the standard auth controller.
    def artefactFile = "${basedir}/${artefactPath}/${artefactName}"
    if (new File(artefactFile).exists()) {
        ant.input(
            addProperty: "${args}.${artefactName}.overwrite",
            message: "${artefactName} already exists. Overwrite? [y/n]")

        if (ant.antProject.properties."${args}.${artefactName}.overwrite" == "n") {
            return
        }
    }

    // Copy the template file to the 'grails-app/controllers' directory.
    templateFile = "${shiroCasPluginDir}/src/templates/artifacts/${templatePath}/${templateName}"
    if (!new File(templateFile).exists()) {
        ant.echo("[Shiro plugin] Error: src/templates/artifacts/${templatePath}/${templateName} does not exist!")
        return
    }

    ant.copy(file: templateFile, tofile: artefactFile, overwrite: true)

    // Perform any custom processing that may be required.
    if (c) {
        c.delegate = [ artefactFile: artefactFile ]
        c.call()
    }

    event("CreatedFile", [artefactFile])
}

setDefaultTarget 'createCasRealm'
