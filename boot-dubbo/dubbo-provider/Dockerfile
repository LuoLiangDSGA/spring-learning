FROM openjdk:8-jre-alpine

MAINTAINER luoliang

ENV DUBBO_IP_TO_REGISTRY 192.168.1.7
ENV DUBBO_PORT_TO_REGISTRY 12345

ADD target/dubbo-provider-1.0.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar","/app.jar"]

EXPOSE 12345

