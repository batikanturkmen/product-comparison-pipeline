FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY ./build/libs/product-retrieval-service-*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
