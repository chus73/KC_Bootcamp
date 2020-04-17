FROM openjdk:8-jdk-alpine
VOLUME /tmp
RUN mkdir -p output
RUN mkdir -p logs
COPY config config
COPY *.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]