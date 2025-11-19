FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Health check (opcional, mas recomendado)
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:${PORT:-9090}/actuator/health || exit 1

EXPOSE 9090
ENTRYPOINT ["sh", "-c", "java -jar -Dserver.port=${PORT:-9090} app.jar"]