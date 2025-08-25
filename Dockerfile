# Stage 1: Build the application
#FROM eclipse-temurin:21-jre-jammy AS builder
FROM eclipse-temurin:17.0.16_8-jdk-jammy AS builder

WORKDIR /workspace

# Copy the Gradle wrapper
COPY gradlew .
COPY gradle ./gradle

# Copy the build configuration files
COPY settings.gradle.kts .
COPY build.gradle.kts .

# Copy the source code
RUN mkdir -p modules/
COPY modules/web ./modules/web
COPY modules/stock ./modules/stock

# Build the application
RUN chmod +x ./gradlew
RUN ./gradlew :modules:web:bootJar

# Stage 2: Create the final production image
# FROM eclipse-temurin:21-jre-jammy
FROM eclipse-temurin:17.0.16_8-jdk-jammy
WORKDIR /app

# Create a non-root user
RUN useradd -m -s /bin/bash appuser
USER appuser

# Copy the executable JAR from the builder stage
COPY --from=builder /workspace/build/libs/*.jar ./application.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "./application.jar"]
