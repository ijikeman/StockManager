FROM gradle:8.14.2-jdk17-jammy AS builder
WORKDIR /app
COPY . .
RUN ./gradlew :modules:web:bootJar --no-daemon

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/modules/web/build/libs/*.jar ./app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
