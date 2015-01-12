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
* MongoDB

#### Build
* mvn clean package (this produces opendash.jar in the target folder)

#### Run (in place for development purposes)
* mvn clean package spring-boot:run

#### Deploy
* java -jar opendash.jar

This starts OpenDashboard on port 8080. Changing the server port (and other properties) can be done on the command line (http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html)
*************************************************************************************
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
