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

# Copy the app from the host to the docker.
ADD target.tar.gz /opt/opendash/

# Open the docker port. (Still have to map the host port with run command)
EXPOSE 8080

# CMD will be called when starting this container.
<<<<<<< HEAD:Dockerfile
CMD java -jar -Djava.security.egd=file:/dev/./urandom opt/opendash/target/opendash-0.1-SNAPSHOT.jar
=======
#CMD java -jar opt/opendash/target/opendash-0.1-SNAPSHOT.jar
>>>>>>> ddc5fb9efe89af9b12eaa2ad4f23e69441d468b0:Dockerfile
