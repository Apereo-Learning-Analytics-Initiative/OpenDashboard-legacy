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

### Pick a name for your card

OpenDashboard relies heavily on convention over code so your card name is important - choose wisely. In our example we'll be building an RSS Feed Reader Card so I'm going to call my card rssreader.

### Add your card name to the card list file src/main/resources/cards/cards.txt

`LTI,openlrs,someothercards,<your card name>`

for example:

`LTI,openlrs,rssreader`

### Next create json descriptor file for your card

This will contain your card's default configuration. Place this file in src/main/resources/cards and name it <your card name>.json (e.g. rssreader.json). The file will have the following format:

`{"cardType":"some type", "name":"some name", "description":"some description", "imgUrl":"path or url to an image"}`

for example:

`{"cardType":"rssreader", "name":"RSS Reader", "description":"Use this card to display an RSS feed","imgUrl":"/img/rss.png"}`
