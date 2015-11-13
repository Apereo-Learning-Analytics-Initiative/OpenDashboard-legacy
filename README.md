OpenDashboard
============================
A web application that provides a framework for displaying visualizations and data views called "cards". Cards represent a single discrete visualization or data view but share an API and data model.
*************************************************************************************
Current Status
----------------
* Early stage development
* Support for
 * Inbound LTI launch
 * Proxied LTI launch via the LTI Card
 * Integration with OpenLRS via the OpenLRS Card
 
*************************************************************************************
## Technical Overview
OpenDashboard is a Java application built with Spring Boot (http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-documentation).

### Requirements
* JDK 7+
* Maven 3+
* MongoDB or redis

#### Build
* mvn clean package (this produces opendash*.jar in the target folder)

## Application Properties
There is one properties file for the application:  
application.properties

There are, however, multiple properties that must be configured.  A security property and a data storage property must be set.  

###Security Properties
Two properties are available.  saml and basic
####saml
Opendash has a Security Assertion Markup Language configuration.  The two properties spring.profiles.active and the feautures.saml must be set.

A command line example with mongo data store:
* mvn -Drun.jvmArguments="-Dspring.profiles.active=mongo,saml -Dfeatures.saml=true" -Dspring.profiles.active=mongo,saml -Dfeatures.saml=true clean package spring-boot:run
####basic
The basic security property uses Spring Security.

A command line example with mongo data store:
* mvn -Drun.jvmArguments="-Dspring.profiles.active=mongo,basic" -Dspring.profiles.active=mongo,basic clean package spring-boot:run

###Data Storage Properties
Two properties are available.  inmemory, mongo, mongo-multitenant and redis
####inmemory
Currently in memory data storage is h2.

A command line example with basic security:
* mvn -Drun.jvmArguments="-Dspring.profiles.active=inmemory,basic" -Dspring.profiles.active=inmemory,basic clean package spring-boot:run

####mongo and mongo-multitenant
Two configurations exist for mongo.  mongo use a single database in repository for all users of the app, mongo-multitenant will dynamically create new databases in the same data warehouse for each tenant.   Just as saml had a feature flag, mongo-multitenant must also set features.multitenant to true.

A command line examples with basic security:
* mvn -Drun.jvmArguments="-Dspring.profiles.active=mongo,basic" -Dspring.profiles.active=mongo,basic clean package spring-boot:run
 * mvn -Drun.jvmArguments="-Dspring.profiles.active=mongo-multitenant,basic -Dfeatures.multitenant=true" -Dspring.profiles.active=mongo-multitenant,basic -Dfeatures.multitenant=true clean package spring-boot:run

####redis
Lastly redis is also an option for data storage.

A command line examples with basic security:
* mvn -Drun.jvmArguments="-Dspring.profiles.active=redis,basic" -Dspring.profiles.active=redis,basic clean package spring-boot:run

Adding a new properties file or alternatively -Dspring.config.location can be used in the command line to override the packaged property file.

#### Run (in place for development purposes)
If you want to use data storage via mongo, mongo-multitent or redis or saml security, the active profile will need to be sent in as a command line arg.
Otherwise the default profile (inmemory data store, basic spring security) will be used.
To run the default profile use the command line arguments:
* mvn clean package spring-boot:run

#### Deploy
Depending on whether mongo or redis is used, the active profile will need to be sent in as a command line arg
* java -jar -Dspring.profiles.active=mongo,basic target/opendash*.jar
* java -jar -Dspring.profiles.active=redis,basic target/opendash*.jar

#### Test
* mvn clean test
* mvn -Dspring.profiles.active=mongo,basic clean test
* mvn -Dspring.profiles.active=redis,basic clean test

This starts OpenDashboard on port 8080. Changing the server port (and other properties) can be done on the command line (http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html)
*************************************************************************************

## Manual Development Environment Setup

### Install the following required supporting components
* [JDK 7+] (https://jdk7.java.net/download.html)
* [MongoDB] (http://docs.mongodb.org/manual/installation/)
* [Maven 3] (http://maven.apache.org/download.cgi#Installation)

### Install OpenDashboard
OpenDashboard source code is stored in a git repository so you will need to have [git] (http://git-scm.com/downloads) installed on your computer.

* Create a new directory for the project. Navigate to the newly created directory and then run these git commands

    `git init`
    
    `git clone https://github.com/Apereo-Learning-Analytics-Initiative/OpenDashboard.git`
    
* This creates a new directory named OpenDashboard that contains the source code for the OpenDashboard project.

**************************************************************************************

## Accessing OpenDashboard via LTI
Currently OpenDashboard can only be accessed via an LTI tool launch. The LTI tool launch endpoint is http(s)://your server/lti . The default OAuth consumer key and secret values are defined in src/main/resources/application.properties - these values can and should be overriden in local properties.
*************************************************************************************

## Preconfigured Dashboards

OpenDashboard provides the ability to preconfigure a collection of dashboards. This gives administrators the ability to define card layout and configuration for their entire OpenDashboard instance and removes the need for end users to configure their own dashboards.

