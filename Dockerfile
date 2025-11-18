# Fase de build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Baixa dependências primeiro (usa cache do Docker)
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Fase de execução
FROM eclipse-temurin:21-jre-slim
WORKDIR /app
EXPOSE 9090

# Copia o JAR e define usuário não-root por segurança
COPY --from=build /app/target/OlhoNoBoleto-0.0.1-SNAPSHOT.jar app.jar
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring

ENTRYPOINT ["java", "-Dserver.port=9090", "-jar", "app.jar"]