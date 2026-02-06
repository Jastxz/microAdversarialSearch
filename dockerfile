# ---- Runtime ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
RUN mkdir -p /app/data && chown -R spring:spring /app

COPY target/*.jar /app/app.jar
RUN chown spring:spring /app/app.jar
USER spring:spring

EXPOSE 5555
ENTRYPOINT ["java", "-jar", "/app/app.jar"]