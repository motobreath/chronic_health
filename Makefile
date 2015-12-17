# Makefile for Grails projects
# ============================
#
# This makefile can be used to build and install Grails projects, and to
# create RPMs. It has been used in Redhat Enterprise Linux 6.7 systems,
# but should be usable in other environments without too much modification.
#
#
# The RPM building process
# ========================
#
# This makefile can be used to make an RPM. It does this by creating the spec
# file from a template, and then making a source tarball that includes that
# spec file. Then "rpmbuild -ta <tarball>" is run to create the RPM.
#
# It is intended to be run as a non-root user and not in a mock environment.
#
# The spec.in file is a close companion to this makefile. There are several
# variables in it that are modified by this makefile when creating the actual
# spec file.
#
#
# Requirements for RPM
# ====================
#
# To support building an RPM of a Grails application, the following preparation
# is needed:
#
#   * The "app.version" and "app.release" properties should be set in
#     application.properties. If in doubt, set app.version to 1.0 and
#     app.release to 1.
#   * The spec template file, spec.in, should be modified. A few of its values
#     can be left as is, especially those that are in ${var}, which will
#     be set automatically when the actual spec file is produced.
#   * The install target of this makefile should be modified.
#   * "Requirements for building" must be met (install java and grails)
#     (see below).
#
#
# Installation tasks requiring root
# ---------------------------------
#
# Any installation tasks that require root privileges (such as setting file
# ownership) should be performed in one of the event triggers in the spec file.
# The %post section is good for this. Example:
#
# %post
# chown -R tomcat.tomcat /etc/%{name}
#
# If you don't intend to use this makefile for bare-metal installation, then
# you're done. But if you do, any privileged commands in the makefile's install
# target _must_ be enclosed in a conditional:
#
#	if [ "`id -u`" = "0" ]; then \
#		# do root-requiring commands here; \
#	fi
#
#
# Requirements for building
# =========================
#
# These are the requirements for building the application. In other words, to
# generate the WAR file.
#
# 1. Grails of the proper version (matching app.grails.version in the
#    application.properties file) must be installed.
#
#    If you want to give it a go with some other version of Grails, you
#    have a couple of options:
#
#       A. Edit application.properties and put in the version of grails that
#          is installed.
#
#       B. Edit this makefile, and for the grails target, remove "grails-version"
#          as a dependency.
#
# 2. The JDK should be installed, and JAVA_HOME must be set such that
#    $JAVA_HOME/bin/java and $JAVA_HOME/bin/javac are available. (This seems
#    to be a requirement for running grails; merely having these programs on
#    the PATH is not sufficient.)
#
#    If you've installed the Oracle Java 8 RPMs, then JAVA_HOME can probably
#    be determined automatically. If not, find out where javac is installed
#    and set JAVA_HOME accordingly.
#
# As of November 2015, the Oracle Java 8 RPMs can be installed in Redhat as follows:
#
#    $ sudo subscription-manager repos --enable rhel-6-server-thirdparty-oracle-java-rpms
#    $ sudo yum install java-1.8.0-oracle.x86_64 java-1.8.0-oracle-devel.x86_64


# VERSION
# -------
# The application's version. To set the version, edit application.properties.
# When we generate the spec file for RPM building, this value will be inserted.
VERSION=$(shell grep app.version application.properties | cut -d '=' -f 2)

# RELEASE
# -------
# The release. This is only relevant when building an RPM. It's also extracted
# from application.properties. The value will be inserted into the spec file
# when building an RPM.
RELEASE=$(shell grep app.release application.properties | cut -d '=' -f 2)

# APPNAME
# -------
# The application name, capitalized as is conventional with Grails.
APPNAME=$(shell grep app.name application.properties | cut -d '=' -f 2)

# APPLOWER
# --------
# The application name in lower case characters. It will become part of the
# RPM's filename. It's inserted into the spec file as the app's name.
APPLOWER=$(shell echo $(APPNAME) | tr '[:upper:]' '[:lower:]')

# APPGRAILS
# ---------
# The version of grails for which the application was written.
APPGRAILS=$(shell grep app.grails.version application.properties | cut -d '=' -f 2)

# SRCNAME
# -------
# Name+version. It is part of the filename of the source tarball.
SRCNAME=$(APPLOWER)-$(VERSION)

# SRCBALL
# -------
# Filename of the source tarball.
SRCBALL=$(SRCNAME).tar.gz

# SRCWORK
# -------
# Temporary directory used when making the source tarball. After making the
# source tarball, cleanup consists of removing this directory.
SRCWORK=src-work

# SRCDIR
# ------
# Relative directory where source is copied when making the source tarball.
SRCDIR=$(SRCWORK)/$(SRCNAME)

# WAR
# ---
# The output file when the WAR file is built.
WAR=target/$(APPNAME).war

# JAVA_HOME
# ---------
# This must be set in order to run grails (which we do for the war target).
# grails seems to want to execute "$JAVA_HOME/bin/javac". Having javac on the
# PATH alone is not sufficient. If JAVA_HOME is not set, we'll set it here to
# the value that works with the Oracle Java RPMs for Redhat
# (e.g., java-1.8.0-oracle.x86_64).
JAVA_HOME?=/usr/lib/jvm/java

# RUNGRAILS
# ---------
# Command to execute grails, setting JAVA_HOME as required.
RUNGRAILS=JAVA_HOME=$(JAVA_HOME) grails

# RPM stuff

# SPEC
# ----
# The spec file as created in the spec target.
SPEC=$(APPLOWER).spec


# *****************
# *--  TARGETS  --*
# *****************

default: war

# The app-properties target
# -------------------------
# Make sure we got all of the required values from application.properties.
app-properties:
	$(info Verifying values from application.properties...)
	@if [ ! -e application.properties ]; then \
		echo "Error: Required file application.properties missing"; \
		exit 1; \
	fi
	@if [ "$(VERSION)" = "" ]; then \
		echo "Error: Unable to determine application version (app.version) from application.properties"; \
		exit 1; \
	fi
	$(info app.version => VERSION=$(VERSION))
	@if [ "$(APPNAME)" = "" ]; then \
		echo "Error: Unable to determine application name (app.name) from application.properties"; \
		exit 1; \
	fi
	$(info app.name => APPNAME=$(APPNAME); APPLOWER=$(APPLOWER))
	@if [ "$(RELEASE)" = "" ]; then \
		echo "Error: Unable to determine application release (app.release) from application.properties"; \
		exit 1; \
	fi
	$(info app.release => RELEASE=$(RELEASE))
	@if [ "$(APPGRAILS)" = "" ]; then \
		echo "Error: Unable to determine grails version (app.grails.version) from application.properties"; \
		exit 1; \
	fi
	$(info app.grails.version => APPGRAILS=$(APPGRAILS))
	@echo application.properties is OK.

# The grails-present target
# -------------------------
# Succeed if grails is on the path.
grails-present: ; @which grails > /dev/null

# The grails-version target
# -------------------------
# Succeed if the grails version matches the version in application.properties.
# Running grails is time-consuming, even just to get its version, so we only
# do it when necessary.
grails-version:
	$(info Checking grails version...)
	$(eval GRAILS_VERSION := $(shell $(RUNGRAILS) -version | perl -pe 's/^.*\s(\d)/$$1/'))
	@if [ $(GRAILS_VERSION) != $(APPGRAILS) ]; then \
		echo "Error: Actual Grails version $(GRAILS_VERSION) does not match required version: $(APPGRAILS)"; \
		exit 1; \
	fi
	@echo Grails is the desired version for this application: $(APPGRAILS)

# The grails target
# -----------------
# Succeed if grails is available and the correct version.
grails: grails-present grails-version

# The java target
# ---------------
# Succeed if java and javac are available in $JAVA_HOME/bin, which is what
# grails requires. Grails may not provide helpful feedback if they aren't,
# so we'll check it in the makefile and provide a useful error message.
java:
	$(info Verifying java...)
	$(info JAVA_HOME=$(JAVA_HOME))
	@if [ ! -x $(JAVA_HOME)/bin/javac -o ! -x $(JAVA_HOME)/bin/java ]; then \
		echo "Error: Invalid JAVA_HOME (current value: $(JAVA_HOME))"; \
		echo JAVA_HOME should be set so that \$$JAVA_HOME/bin/javac and \$$JAVA_HOME/bin/java; \
		echo are executable.; \
		exit 1; \
	fi

# The WAR target
# --------------
# If grails runs into a problem, it may output invalid terminal codes resulting
# in a blank screen. We'll suppress those by redirecting its output through cat.
# (Note that using -plain-output does not prevent the terminal-clearing issue.)
war: $(WAR)

$(WAR): app-properties java grails
	$(info Building WAR file...)
	$(RUNGRAILS) war -stacktrace -plain-output 2>&1 | cat

# The spec target
# ---------------
# Creates a spec file suitable for building an RPM. It uses spec.in as
# a template and creates "<appname>.spec", substituting values for each
# ${varname}.
spec: application.properties app-properties spec.in
	perl -pe 's/\$$\{version\}/$(VERSION)/g;s/\$$\{release\}/$(RELEASE)/g;s/\$$\{name\}/$(APPLOWER)/g;s/\$$\{capname\}/$(APPNAME)/g' spec.in > $(SPEC)

# The src target
# --------------
# Creates a source tarball suitable for building an RPM.
# Its goals:
#   - Include all source necessary for building the application using grails
#   - Include the Makefile, whose "install" target will be used while building the RPM
#   - Include a spec file, with the version, release, and other values inserted,
#     which can be used by rpmbuild when "rpmbuild -ta <tarball>" is executed.
#   - Don't include unnecessary stuff, like the "target" directory, which could be large,
#     and git metadata
#   - Use rsync whenever possible.
#   - Include the additional, project-specific files that should be part of the RPM:
#   --- The sample configuration file
#   --- The health checking scripts
.PHONY: src
src: spec
	rm -fr $(SRCWORK)
	rm -f $(SRCBALL)
	mkdir -p $(SRCDIR)
	cp $(SPEC) $(SRCDIR)
	rsync -qa . $(SRCDIR) --exclude $(SRCWORK) --exclude target\* --exclude .git\*
	(cd $(SRCWORK) && tar -c $(SRCNAME) | gzip > ../$(SRCBALL))
	rm -fr $(SRCWORK)
	@echo Created source tarball:
	@ls --color=auto -l $(shell pwd)/$(SRCBALL)

# The clean target
# ----------------
# Removes most generated files. Doesn't remove downloaded libraries, though.
# That sort of thing would be done in a "superclean" target. Which we don't have.
# Because honestly, who throws a bucket?
clean:
	rm -f $(WAR)
	rm -fr $(SRCWORK)
	rm -f $(APPLOWER)-*.tar.gz
	rm -f $(SPEC)

# The install target
# ------------------
# Yep, installs the app. It's suitable for actual installation because it won't
# overwrite the configuration file, and it will set permissions and ownership.
# It's also suitable for making an RPM because:
#  a) It honors the DESTDIR variable
#  b) It is fine with not running as root (or even in a mock environment)
install: app-properties
	$(info Installing $(APPNAME); DESTDIR=$(DESTDIR))
	install -d $(DESTDIR)/var/lib/tomcat/webapps
	install -m644 target/$(APPNAME).war $(DESTDIR)/var/lib/tomcat/webapps
	

# The rpm target
# --------------
# Builds the RPM. The heavy lifting is done in the src, spec, and install
# targets.
rpm: src spec
	$(info Building RPM for version $(VERSION), release $(RELEASE))
	rpmbuild -ta $(SRCBALL)

