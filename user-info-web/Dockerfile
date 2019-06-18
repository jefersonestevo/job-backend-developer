FROM jbd.user.web/baseimage as build

WORKDIR /usr/src/app

COPY pom.xml .
COPY ./user-info-web user-info-web

RUN mvn clean install -DskipTests

FROM openjdk:8-jre-alpine
VOLUME /tmp
ARG JAR_FILE

COPY --from=build /usr/src/app/user-info-web/target/*.jar /app.jar

VOLUME /usr/src/app/config

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
