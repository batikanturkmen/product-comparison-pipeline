FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY ./build/libs/naming-server-*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
