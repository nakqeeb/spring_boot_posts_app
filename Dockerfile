FROM eclipse-temurin:22-jdk-alpine
# Set environment variables (optional but recommended for flexibility)
ENV SERVER_PORT=5000
ENV SPRING_PROFILES_ACTIVE=prod
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]