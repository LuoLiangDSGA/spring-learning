FROM openjdk:8-jre-alpine

MAINTAINER luoliang ll9420416@icloud.com

ADD target/dubbo-consumer-1.0.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar","/app.jar"]

EXPOSE 8081

