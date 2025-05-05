# gateway/Dockerfile
FROM maven:3.8.4-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/gateway-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8800
ENTRYPOINT ["java", "-jar", "app.jar"]