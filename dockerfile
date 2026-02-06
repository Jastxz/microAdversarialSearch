# ---- Builder ----
FROM maven:3.9.11-amazoncorretto-24-al2023 AS builder
WORKDIR /app

# Copiar y descargar dependencias del proyecto
COPY pom.xml .
RUN mvn -B -ntp -q dependency:go-offline

# Compilar la aplicación
COPY . .
RUN mvn -B -ntp clean package -DskipTests

# ---- Runtime ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
RUN mkdir -p /app/data && chown -R spring:spring /app

COPY --from=builder /app/target/*.jar /app/app.jar
RUN chown spring:spring /app/app.jar
USER spring:spring

EXPOSE 5555
ENTRYPOINT ["java", "-jar", "/app/app.jar"]