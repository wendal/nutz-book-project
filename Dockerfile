FROM java:8-jdk

MAINTAINER wendal "wendal1985@gmail.com"

ENV CATALINA_HOME /usr/local/tomcat
ENV PATH $CATALINA_HOME/bin:$PATH
RUN mkdir -p "$CATALINA_HOME"

RUN cd $CATALINA_HOME \
	&&wget -O tomcat.tar.gz http://www.us.apache.org/dist/tomcat/tomcat-8/v8.0.22/bin/apache-tomcat-8.0.22.tar.gz \
	&& tar -xvf tomcat.tar.gz --strip-components=1 \
	&& cd $CATALINA_HOME \
	&& rm bin/*.bat \
	&& rm tomcat.tar.gz* && rm -fr /usr/local/tomcat/webapps/*

RUN cd $CATALINA_HOME/webapps && wget http://nutz.cn/nutzbook/rs/nutzbook.war \
	&& unzip -d ROOT nutzbook.war && rm -y nutzbook.war
	
WORKDIR $CATALINA_HOME

VOLUME /usr/local/tomcat/webapps
	
EXPOSE 8080
CMD ["catalina.sh", "run"]