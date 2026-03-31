# Dockerfile - Multi-stage build for AnonChat Spring Boot Backend
# This file enables Docker deployment on Render.com or any Docker-compatible platform

# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy the entire project
COPY . .

# Build the Spring Boot JAR
# Skip tests for faster build, but you can remove -DskipTests to run tests
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /build/api-gateway/target/api-gateway-1.0.0.jar app.jar

# Expose the port Spring Boot runs on
EXPOSE 8081

# Run the Spring Boot application with proper Java options
# Environment variables (DB_URL, DB_DIALECT, DB_DRIVER) are provided by Render
CMD ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
