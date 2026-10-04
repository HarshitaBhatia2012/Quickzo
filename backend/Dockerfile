# Stage 1: Build Spring Boot application using Java 21 and Gradle
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

# Copy Gradle wrapper and build configuration
COPY backend/gradle/ gradle/
COPY backend/gradlew backend/gradlew.bat backend/build.gradle backend/settings.gradle ./

# Normalize line endings and grant execute permissions
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# Pre-fetch Gradle dependencies
RUN ./gradlew dependencies --no-daemon || true

# Copy application source code
COPY backend/src/ src/

# Build Spring Boot executable archive (skip tests during container build)
RUN ./gradlew bootJar --no-daemon -x test

# Stage 2: Minimal Java 21 runtime container
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Create and run as unprivileged spring user
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copy compiled jar from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
