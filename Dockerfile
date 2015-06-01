FROM docker pull wendal/docker-jdk-tomcat:jdk8-tomcat8

MAINTAINER wendal "wendal1985@gmail.com"

RUN cd $CATALINA_HOME/webapps && wget http://nutz.cn/nutzbook/rs/nutzbook.war \
	&& unzip -d ROOT nutzbook.war && rm -y nutzbook.war