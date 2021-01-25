FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY ./build/libs/rest-proxy-product-stream-processor-*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
