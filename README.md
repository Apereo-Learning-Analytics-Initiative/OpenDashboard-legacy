OpenDashboard
============================
A web application that provides a framework for displaying visualizations and data views called "cards". Cards represent a single discrete visualization or data view but share an API and data model.

*************************************************************************************

## New version as of February 12th, 2020
Until now, the Dashboard served up both the UX and the Api's required to display the data. 
In keeping with a true Service Oriented Architecture, the two applications are now separate and exist in two repositories. The source code in This repository will not display anything. It is responsible for security, and serving the appropriate UX.

The two repo's required are:
1. This repository
2. OpenDashboard-ux (https://github.com/Apereo-Learning-Analytics-Initiative/OpenDashboard-ux)

To enhance scalability, if the dashboard is being used with the apereo project OpenLRW (https://github.com/Apereo-Learning-Analytics-Initiative/OpenLRW), it is now recommended that they are both configured with the same database.

## Installation
### Prerequisites
* git
* MongoDB 3.6+ [(install instructions)](https://docs.mongodb.com/manual/installation/)
* Maven 3 [(download)](https://maven.apache.org/download.cgi)
* Java 8 [(jdk)](http://openjdk.java.net/)
### Run in Demo mode
#### Using Maven for development purposes
~~~~
Demo can now be seen completely with JUST the OpenDashboard-ux
~~~~

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
#### Change the default admin username and password
You can change the default admin username and/or password using the od.admin.user and od.admin.password properties.
~~~~
mvn clean package spring-boot:run -Dod.admin.user=someOtherUsername -Dod.admin.password=someOtherPassword
~~~~

License
-------
ECL (a slightly less permissive Apache2)
http://opensource.org/licenses/ECL-2.0

Contact
-------
Send questions or comments to the mailing list: opendashboard-user@apereo.org
