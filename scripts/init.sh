#!/bin/bash

echo "🏗️ Building and starting QBike microservices system..."
echo "======================================================"

# Create necessary directories
mkdir -p scripts
mkdir -p services/{uc,position,order}/target
mkdir -p services/intention-service/target
mkdir -p eureka/eureka/target
mkdir -p gateway/target

# Build all services
echo "Building Eureka Server..."
cd eureka/eureka && mvn clean package -DskipTests && cd ../..

echo "Building UC Service..."
cd services/uc && mvn clean package -DskipTests && cd ../..

echo "Building Position Service..."
cd services/position && mvn clean package -DskipTests && cd ../..

echo "Building Intention Service..."
cd services/intention-service && mvn clean package -DskipTests && cd ../..

echo "Building Order Service..."
cd services/order && mvn clean package -DskipTests && cd ../..

echo "Building Gateway..."
cd gateway && mvn clean package -DskipTests && cd ..

# Start all services
echo "Starting all services with Docker Compose..."
docker-compose up --build -d

# Show service status
echo ""
echo "Service Status:"
echo "---------------"
docker-compose ps

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
echo "View logs with: docker-compose logs -f [service-name]"