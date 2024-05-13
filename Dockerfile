FROM openjdk:17
VOLUME /tmp
EXPOSE 8080
COPY target/technical-0.0.1-SNAPSHOT.jar technical.jar
ENTRYPOINT ["java","-jar","/technical.jar"]