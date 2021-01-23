FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY ./build/libs/file-product-stream-processor-*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]