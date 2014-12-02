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
## Developing your own OpenDashboard Card
Step-by-step instructions with examples for developing your own OpenDashboard Card.

### Pick a type for your card

OpenDashboard relies heavily on convention over code so your card type is important - choose wisely. In our example we'll be building an RSS Feed Reader Card so I'm going to call my card type rssreader.

### Add your card type to the card list file src/main/resources/cards/cards.txt

`LTI,openlrs,someothercards,<your card type>`

for example:

`LTI,openlrs,rssreader`

### Next create a json descriptor file for your card

This will contain your card's default configuration. Place this file in src/main/resources/cards and name it <your card type>.json (e.g. rssreader.json). The file will have the following format:

`{"cardType":"some type", "name":"some name", "description":"some description", "imgUrl":"path or url to an image"}`

for example:

`{"cardType":"rssreader", "name":"RSS Reader", "description":"Use this card to display an RSS feed","imgUrl":"/img/rss.png"}`

### Create the HTML for your card

Start by creating a new directory in src/main/resources/public/html/cards that matches your card type (e.g. src/main/resources/public/html/cards/rssreader). Then create two html files in the new directory called card.html and config.html. card.html should contain the UI markup for your card. config.html is typically used to capture configuration information about your card and handles adding an instance of your card to the dashboard. OpenDashboard supports Bootstrap 3 by default but if you wanted to use different styling you would do that here as well.

See src/main/resources/public/html/cards/rssreader/card.html and src/main/resources/public/html/cards/rssreader/config.html for examples.
