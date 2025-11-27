# ============================
# Stage 1 — Build the JAR file
# ============================
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copy Maven configuration and dependencies first (for caching)
COPY pom.xml .
COPY src ./src

# Package the application (skip tests for faster build)
RUN mvn clean package -DskipTests

# ============================
# Stage 2 — Run the built app
# ============================
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose application port
EXPOSE 8085

# Environment variables (can be overridden in docker-compose)
ENV JAVA_OPTS=""
ENV SERVER_PORT=8085

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
