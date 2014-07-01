lti_starter
===========

IMS LTI 2 (with fallback to 1) based starter (sample) application written using Java and Spring Boot

The goal is to have a Java based web app which can serve as the basis (or starting point) for building a fully compliant LTI 1 or 2 based tool without having to manage the complexities of LTI 2 or come up with a strategy for handling the various types of data storage.

Parts based on the data structures and code in tsugi (https://github.com/csev/tsugi) which is a Multi-tenant learning tool hosting platform (http://www.tsugi.org)

Build
-----
This will produce a starter.war file in the *target* directory which can be placed into any standard servlet container.

    mvn install

Quick Run
---------
You can run the app in place to try it out without having to install and deploy a servlet container.

    mvn clean install spring-boot:run

Then go to the following default URL:

    http://localhost:8080/
