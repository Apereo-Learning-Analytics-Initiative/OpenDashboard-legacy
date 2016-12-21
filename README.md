OpenDashboard
============================
A web application that provides a framework for displaying visualizations and data views called "cards". Cards represent a single discrete visualization or data view but share an API and data model.

*************************************************************************************

## Installation
### Prerequisites
* git
* MongoDB 2.6+ [(install instructions)](https://docs.mongodb.com/manual/installation/)
* Maven 3 [(download)](https://maven.apache.org/download.cgi)
* Java 8 [(jdk)](http://openjdk.java.net/)
### Run in Demo mode
#### Using Maven for development purposes
~~~~
mvn clean package spring-boot:run -Drun.profiles=demo
~~~~

This will start the application and enable the demo data providers. By default the application uses port 8081 so you can access the login screen at [http://localhost:8081](http://localhost:8081). You will still need to configure the application to use the demo data providers. This [video](https://youtu.be/tNi50DEtbn0) shows how to configure the demo data providers. If you didn't catch it during the video the default username and password is admin / admin .

These instructions also assume that you are running MongoDB on the same machine as the dashboard application (i.e., MongoDB is accessible at localhost:27017). If you need to configure the application to connect to a different MongoDB address see the [Spring-Boot & MongoDB configuration](http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html) properties.

#### Using a more production-like setup
##### Directory Structure
Create the following directory structure.
* /opt/od/
  * conf
  * lib
  * logs
  * run
  * src
  * build.sh
  * run.sh
###### Add a user to run the application
Create a user to run the application and make them owner of /opt/od/* directories.
~~~~
useradd -c "Boot User" boot
chown -R boot:boot /opt/od
~~~~
###### Checkout the source code
This is a one time operation. Note you'll need to update the git command below with your git username. 
~~~~
cd /opt/od/src
git clone https://<!-- your bitbucket username-->@bitbucket.org/unicon/laq-dashboard.git
~~~~
###### Build Script (build.sh)
From the /opt/od directory execute the build script to create the dashboard executable.
~~~~
#!/bin/sh
cd `dirname $0`
cd src/laq-opendashboard
git pull
mvn -DskipTests=true clean install
cp target/opendash-0.1-SNAPSHOT.jar ../../lib/od.jar
~~~~
###### Run Script (run.sh)
From the /opt/od directory execute the run script to start the application. Note you will need to update the script below with the appropriate MongoDB path. The application listens on port 8081.
~~~~
#!/bin/sh
cd `dirname $0`
APP_HOME="$PWD"
PID_FILE=$APP_HOME/run/od.pid
JAR_PATH=$APP_HOME/lib/od.jar
cd $APP_HOME
case "$1" in
"start")
 if [ -f $PID_FILE ]; then
 exit 1
 fi
 java \
 -Dspring.profiles.active=demo \
 -Dlogging.path=/opt/od/logs/ \
 -Dspring.data.mongodb.uri=<!-- mongodb uri --> \
 -jar $JAR_PATH &
 echo $! > $PID_FILE
 ;;
"stop")
 if [ ! -f $PID_FILE ]; then
 exit 1
 fi
 kill `cat $PID_FILE`
 rm -f $PID_FILE
 ;;
*)
 echo "Usage: $0 start|stop"
 ;;
esac
exit 0
~~~~
###### Automated Start (e.g., AWS Auto-scale)
~~~~
#!/bin/bash
yum update -y
bash
cd /opt/od
rm /opt/od/run/*.pid
rm /opt/od/*.log
rm /opt/od/logs/*.log
su boot -c "sh build.sh"
su boot -c "sh run.sh start"
~~~~

License
-------
ECL (a slightly less permissive Apache2)
http://opensource.org/licenses/ECL-2.0

Contact
-------
Send questions or comments to the mailing list: opendashboard-user@apereo.org
