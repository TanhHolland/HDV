#!/bin/bash

echo "🏗️ Building and starting nhom4 microservices system..."
echo "======================================================"

echo "Building Gradle services (nhom4-user, intention, order, position)..."
./gradlew bootJar -x test

echo "Building Eureka Server..."
cd eureka && mvn clean package -DskipTests && cd ..

echo "Building Gateway..."
cd gateway && mvn clean package -DskipTests && cd ..

echo "Starting all services with Docker Compose..."
docker compose up --build -d

echo ""
echo "Service Status:"
echo "---------------"
docker compose ps

echo ""
echo "✅ All services are starting up!"
echo ""
echo "Service URLs:"
echo "- Eureka Dashboard: http://localhost:8761"
echo "- RabbitMQ Management: http://localhost:15672 (guest/guest)"
echo "- API Gateway: http://localhost:8800"
echo "- MySQL: localhost:3306"
echo "- Redis: localhost:6379"
echo ""
echo "View logs with: docker compose logs -f [service-name]"
