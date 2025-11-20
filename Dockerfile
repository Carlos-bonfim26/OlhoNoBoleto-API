FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/OlhoNoBoleto-0.0.1-SNAPSHOT.jar app.jar

# Instalar haveged para melhorar a entropia (opcional, mas pode ajudar)
RUN apk add --no-cache haveged
# Solução alternativa para SecureRandom
ENV JAVA_OPTS="-Xmx256m -Xss256k -XX:+UseSerialGC -Djava.security.egd=file:/dev/./urandom -Dsecurerandom.source=file:/dev/./urandom -Dspring.jmx.enabled=false -Dspring.main.lazy-initialization=true -Dspring.main.log-startup-info=false"

EXPOSE 10000
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]