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
Depending on whether mongo or redis is used, the active profile will need to be sent in as a command line arg
* mvn -Drun.jvmArguments="-Dspring.profiles.active=mongo" clean package spring-boot:run
* mvn -Drun.jvmArguments="-Dspring.profiles.active=redis" clean package spring-boot:run

#### Deploy
Depending on whether mongo or redis is used, the active profile will need to be sent in as a command line arg
* java -jar -Dspring.profiles.active=mongo target/opendash*.jar
* java -jar -Dspring.profiles.active=redis target/opendash*.jar

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

## Developing your own OpenDashboard Card
Step-by-step instructions with examples for developing your own OpenDashboard Card.

### Pick a type for your card

OpenDashboard relies heavily on convention over code so your card type is important - choose wisely. In our example we'll be building an RSS Feed Reader Card so I'm going to call my card type rssreader.

### Create the HTML for your card

Start by creating a new directory in src/main/resources/public/cards that matches your card type (e.g. src/main/resources/public/cards/rssreader). Then create an html file in the new directory called view.html. view.html should contain the UI markup for your card. OpenDashboard supports Bootstrap 3 by default but if you wanted to use different styling you would do that here as well.

See src/main/resources/public/cards/rssreader/view.html for an example.

### Create the AngularJS Module for your card

This file contains the supporting Javascript for your card. Create a file called module.js in your card directory. Use this file to define the AngularJS module and any supporting controllers and services.

See src/main/resources/public/cards/rssreader/module.js for an example.

Also use this file to register your card with OpenDashboard and define its configuration data. 

For example:

`registryProvider.register('rssreader',
 {
		title: 'RSS Reader',
		description: 'Use this card to display an RSS feed',
		imgUrl: '/cards/rssreader/rss.png',
		cardType: 'rssreader',
		styleClasses: 'od-card col-xs-12 col-md-6',
		config: [
		  {field:'url',fieldName:'URL',fieldType:'url',required:true}
		]
	});`
	
Note, cardType should match the name of your card directory.

### Update the AngularJS application to include your module

Open src/main/resources/public/framework/od-app.js and add your card module to the application dependencies. For example:

`angular
	.module('OpenDashboard', ['ngRoute', 'OpenDashboardFramework', 
	                          'angularCharts',
	                          'od.cards.version', 'od.cards.lti', 'od.cards.openlrs', 'od.cards.openlrsstats', 'od.cards.rssreader'])`

### Add your module js file to the main application page

Open src/main/resources/templates/od.html and add a script tag at the bottom of the page pointing to your module js file. For example:

`<script src="../static/cards/rssreader/module.js" th:src="@{/cards/rssreader/module.js}"></script>`

### Optionally create your own data services

If you need to proxy requests to another data source or call an external web service you may need to add Java code to OpenDashboard to do this. See src/main/java/od/cards/openlrs/OpenLRSCardController.java for an example.

*************************************************************************************
## Preconfigured Dashboards

OpenDashboard provides the ability to preconfigure a collection of dashboards. This gives administrators the ability to define card layout and configuration for their entire OpenDashboard instance and removes the need for end users to configure their own dashboards.

Preconfigured dashboards are defined in JSON format (see src/main/resources/dashboards.json) for an example. By default the preconfigured dashboards option is disabled; to enable preconfigured dashboards set the dashboards.preconfigured.allow property to true. By default src/main/resources/dashboards.json is used as the dashboards definition file but the location can be specified using the dashboards.jsonfile property. 
