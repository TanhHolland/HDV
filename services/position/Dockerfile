FROM openjdk:20-jdk-slim

WORKDIR /app

COPY target/position-1.0-SNAPSHOT.jar app.jar

EXPOSE 8003

ENTRYPOINT ["java", "-jar", "app.jar"]