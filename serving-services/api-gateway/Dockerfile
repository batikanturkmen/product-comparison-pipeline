FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY ./build/libs/api-gateway-*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
