ARG SERVICE_NAME
ARG SERVICE_PORT=8080

FROM eclipse-temurin:21-jdk AS builder
ARG SERVICE_NAME
WORKDIR /app
COPY gradlew settings.gradle build.gradle gradle.properties ./
COPY gradle gradle
COPY ${SERVICE_NAME}/build.gradle ${SERVICE_NAME}/build.gradle
COPY ${SERVICE_NAME}/src ${SERVICE_NAME}/src
RUN chmod +x gradlew && ./gradlew :${SERVICE_NAME}:bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre
ARG SERVICE_NAME
ARG SERVICE_PORT
WORKDIR /app
COPY --from=builder /app/${SERVICE_NAME}/build/libs/*.jar app.jar
EXPOSE ${SERVICE_PORT}
ENTRYPOINT ["java", "-jar", "app.jar"]
