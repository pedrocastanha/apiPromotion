    # Etapa de build
    FROM maven:3.8.5-openjdk-17-slim AS builder
    WORKDIR /app
    COPY pom.xml .
    COPY src ./src
    RUN mvn clean package -DskipTests

    # Etapa de runtime
    FROM openjdk:17-jdk-slim
    WORKDIR /app
    COPY --from=builder /app/target/clinic-scheduler-1.0.0.jar app.jar
    ENTRYPOINT ["java","-jar","app.jar"]
