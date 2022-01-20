FROM maven:3.5-jdk-8-alpine
WORKDIR /app
COPY src ./src
COPY pom.xml ./
RUN mvn package

FROM openjdk:8-jre-alpine
WORKDIR /app
ENV java = /usr/bin/java
COPY --from=0 /app/target/semanticharmonysocialnetwork-1.0-jar-with-dependencies.jar ./

ENTRYPOINT ["java", "-jar", "semanticharmonysocialnetwork-1.0-jar-with-dependencies.jar"] 
CMD ["-m","-id"]

