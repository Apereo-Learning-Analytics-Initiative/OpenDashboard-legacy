###############################################################################
# Copyright 2015 Unicon (R) Licensed under the
# Educational Community License, Version 2.0 (the "License"); you may
# not use this file except in compliance with the License. You may
# obtain a copy of the License at
#
# http://www.osedu.org/licenses/ECL-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an "AS IS"
# BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
# or implied. See the License for the specific language governing
# permissions and limitations under the License.
###############################################################################

# DOCKER-VERSION 1.8.1
FROM      ubuntu:14.04
MAINTAINER Jason Brown "jbrown@unicon.net"

# make sure the package repository is up to date
RUN echo "deb http://archive.ubuntu.com/ubuntu trusty main universe" > /etc/apt/sources.list
RUN apt-get -y update

# install python-software-properties (so you can do add-apt-repository)
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y -q python-software-properties software-properties-common

# Install Java.
RUN \
  echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
  add-apt-repository -y ppa:webupd8team/java && \
  apt-get update && \
  apt-get install -y oracle-java8-installer && \
  rm -rf /var/lib/apt/lists/* && \
  rm -rf /var/cache/oracle-jdk8-installer

# Create a directory for the app to live
RUN mkdir opt/opendash/

# Open the docker port. (Still have to map the host port with run command)
EXPOSE 8080

# CMD will be called when starting this container.
WORKDIR /opt/opendash/
CMD java -server -jar -Djava.security.egd=file:/dev/./urandom -Dspring.config.location=/opt/opendash/dev.properties opendash-0.1-SNAPSHOT.jar

