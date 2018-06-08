FROM openjdk:8-jdk-alpine

MAINTAINER luoliang ll9420416@icloud.com
ADD target/dubbo-provider-1.0.0.jar app.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar","/app.jar"]

EXPOSE 12345
