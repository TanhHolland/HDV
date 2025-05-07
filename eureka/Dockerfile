FROM maven:3.8.4-openjdk-17 AS builder
WORKDIR /app

# Đặt encoding cho Maven và Java
ENV LANG=C.UTF-8
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"
ENV MAVEN_OPTS="-Dfile.encoding=UTF-8"

COPY pom.xml .
# Tạo thư mục trống để tối ưu cache của Maven
RUN mvn dependency:go-offline

COPY src ./src
# Chỉ định rõ encoding
RUN mvn clean package -DskipTests -Dproject.build.sourceEncoding=UTF-8

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/eureka-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]