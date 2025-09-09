FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .

WORKDIR  /app/api

RUN mvn clean package -B -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=build /app/api/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
