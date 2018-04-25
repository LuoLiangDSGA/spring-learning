FROM java:8

VOLUME /tmp

ADD dubbo-provider-1.0.0.jar app.jar

RUN bash -c 'touch /app.jar'

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]