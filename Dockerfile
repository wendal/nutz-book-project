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
	&& unzip -d ROOT nutzbook.war && rm nutzbook.war

RUN echo "db.url=jdbc:mysql://10.10.26.58:3306/x1klIZUwVqJXODpH" >> $CATALINA_HOME/webapps/ROOT/WEB-INF/classes/custom/ && \
	echo "db.username=udKsC4vkxOFB0YDr" >> $CATALINA_HOME/webapps/ROOT/WEB-INF/classes/custom/ && \
	echo "db.password=pqxtomWDCPEuKhHOp" >> $CATALINA_HOME/webapps/ROOT/WEB-INF/classes/custom/ && \
	echo "db.maxActive=10" >> $CATALINA_HOME/webapps/ROOT/WEB-INF/classes/custom/
	
	
WORKDIR $CATALINA_HOME

VOLUME /usr/local/tomcat/webapps

EXPOSE 8080
CMD ["catalina.sh", "run"]