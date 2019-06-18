FROM maven:3.6.0-jdk-8-alpine

WORKDIR /usr/src/app

COPY pom.xml .
COPY ./user-info-web/pom.xml user-info-web/pom.xml
RUN mvn dependency:go-offline
