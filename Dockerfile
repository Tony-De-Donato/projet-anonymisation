FROM eclipse-temurin:21-jdk
ARG JAR_FILE=target/project-anonymization-0.0.1-SNAPSHOT.jar


COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","app.jar"]