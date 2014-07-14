LTI 2 java starter app
======================

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

You can access the H2 console for default in-memory DB (JDBC URL: **jdbc:h2:mem:AZ**) at:

    http://localhost:8080/console

Customizing
-----------
Use the application.properties to control various aspects of the Spring Boot application (like setup your own database connection).
Use the logback.xml to adjust and control logging.

Debugging
---------
To enable the debugging port (localhost:8000) when using spring-boot:run, use the main profile: -Pdebug. Then you can attach any remote debugger (eclipse, intellij, etc.) to localhost:8000. NOTE that the application will pause until you connect the debugger to it.

    mvn clean install spring-boot:run -Pdebug
