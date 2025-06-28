# Build stage
FROM gradle:8.11.1-jdk21-alpine AS build
WORKDIR /app

# Copy Gradle wrapper and configuration files
COPY gradlew* ./
COPY gradle ./gradle
COPY build.gradle settings.gradle gradle.properties ./

# Copy module build files
COPY api/build.gradle ./api/
COPY database/build.gradle ./database/
COPY ui-components/package.json ./ui-components/

# Make gradlew executable
RUN chmod +x ./gradlew

# Download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY api/src ./api/src
COPY database/src ./database/src

# Build the application
RUN ./gradlew :api:bootJar -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -g 1000 spring && \
    adduser -u 1000 -G spring -s /bin/sh -D spring

# Copy JAR from build stage
COPY --from=build /app/api/build/libs/*.jar app.jar

# Set ownership
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring:spring

# Expose port
EXPOSE 8080

# Set JVM options
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]