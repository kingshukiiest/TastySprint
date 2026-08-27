# Stage 1: Build stage using Maven and Java 21
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Package application JAR without running tests
RUN mvn clean package -DskipTests

# Stage 2: Lightweight runtime stage using Eclipse Temurin JRE 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy compiled JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Render injects PORT environment variable (default 8080)
EXPOSE 8080

# Launch Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
