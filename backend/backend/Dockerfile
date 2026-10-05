# Stage 1: Build Spring Boot application using Java 21 and Gradle
FROM gradle:8.14-jdk21 AS builder
WORKDIR /app

# Copy build configuration
COPY build.gradle settings.gradle ./

# Copy application source code
COPY src/ src/

# Build Spring Boot executable archive (skip tests during container build)
RUN gradle bootJar --no-daemon -x test

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
# final working code and connected to github and jira and .....