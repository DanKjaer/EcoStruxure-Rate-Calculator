
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY /target/EcoStruxure-Rate-Calculator-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]