FROM maven:3.9.7-eclipse-temurin-21 AS builder
WORKDIR /app
COPY ../pom.xml ./
COPY ../src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache curl
COPY --from=builder /app/target/*.jar app.jar
WORKDIR /app
EXPOSE 8080
CMD ["java", "-jar", "/app.jar"]