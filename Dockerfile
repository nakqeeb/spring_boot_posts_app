# Stage 1: Build the application
FROM eclipse-temurin:22-jdk-alpine AS build

# Set the working directory
WORKDIR /app

# Copy Maven wrapper and project files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Build the application, skipping tests for faster builds
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:22-jdk-alpine

# Set environment variables from the .properties file
ENV SPRING_DATASOURCE_URL=jdbc:mysql://posts-db.cpu6q68syy3m.us-east-1.rds.amazonaws.com:3306/post_directory \
    SPRING_DATASOURCE_USERNAME=springstudent \
    SPRING_DATASOURCE_PASSWORD=springstudent \
    SPRING_DATA_REST_BASE_PATH=/api \
    SPRING_MAIN_BANNER_MODE=off \
    LOGGING_LEVEL_ORG_HIBERNATE_SQL=debug \
    LOGGING_LEVEL_ORG_HIBERNATE_ORM_JDBC_BIND=trace \
    SPRING_JPA_HIBERNATE_DDL_AUTO=update \
    SECURITY_JWT_SECRET_KEY=3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b \
    SECURITY_JWT_EXPIRATION_TIME=3600000 \
    SPRINGDOC_API_DOCS_PATH=/api-docs \
    SERVER_PORT=8005

# Set the working directory for the app
WORKDIR /usr/app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port (matches SERVER_PORT above)
EXPOSE 8005

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]