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

#### Application Properties
There are two properties files in the application:
application-mongo.properties
application-redis.properties

The properties file used by the application is chosen automatically based on the profile being run.

Alternatively -Dspring.config.location can be used in the command line to override the packaged properties file.

#### Run (in place for development purposes)
Depending on whether mongo or redis is used, the active profile will need to be sent in as a command line arg.
The profile needs to have two command line args, one for unit tests and once for runtime.
* mvn -Drun.jvmArguments="-Dspring.profiles.active=mongo" -Dspring.profiles.active=mongo clean package spring-boot:run
* mvn -Drun.jvmArguments="-Dspring.profiles.active=redis" -Dspring.profiles.active=redis clean package spring-boot:run

#### Deploy
Depending on whether mongo or redis is used, the active profile will need to be sent in as a command line arg
* java -jar -Dspring.profiles.active=mongo target/opendash*.jar
* java -jar -Dspring.profiles.active=redis target/opendash*.jar

#### Test
mvn -Dspring.profiles.active=mongo clean test
mvn -Dspring.profiles.active=redis clean test

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
Currently OpenDashboard can only be accessed via an LTI tool launch. The LTI tool launch endpoint is http(s)://your server/ . The default OAuth consumer key and secret values are defined in src/main/resources/application.properties - these values can and should be overriden in local properties.
*************************************************************************************

## Preconfigured Dashboards

OpenDashboard provides the ability to preconfigure a collection of dashboards. This gives administrators the ability to define card layout and configuration for their entire OpenDashboard instance and removes the need for end users to configure their own dashboards.

Preconfigured dashboards are defined in JSON format (see src/main/resources/dashboards.json) for an example. By default the preconfigured dashboards option is disabled; to enable preconfigured dashboards set the dashboards.preconfigured.allow property to true. By default src/main/resources/dashboards.json is used as the dashboards definition file but the location can be specified using the dashboards.jsonfile property. 
