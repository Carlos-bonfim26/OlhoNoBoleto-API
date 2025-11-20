FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiando o JAR com nome correto
COPY --from=build /app/target/OlhoNoBoleto-0.0.1-SNAPSHOT.jar app.jar

# Otimizações para startup mais rápido da JVM
ENV JAVA_OPTS="-Xss256k -Xmx256m -XX:+UseSerialGC -Dspring.jmx.enabled=false -Dspring.main.lazy-initialization=true"

EXPOSE 10000

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]