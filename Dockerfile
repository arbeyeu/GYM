# ===================================================================
# Stage 1: Build the Spring Boot application using Maven & Java 17
# ===================================================================
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Cache Maven dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build executable jar
COPY src ./src
RUN mvn clean package -DskipTests

# ===================================================================
# Stage 2: Create lightweight runtime image
# ===================================================================
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the compiled jar from the build stage
COPY --from=build /app/target/ironcore-gym-1.0.0.jar app.jar

# Configure environment and port
ENV PORT=8080
EXPOSE 8080

# Run Spring Boot with dynamic PORT binding for cloud hosts
ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-jar", "app.jar"]
