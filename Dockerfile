FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "app.jar"]
