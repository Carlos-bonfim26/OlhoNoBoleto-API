FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -T 1C

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/OlhoNoBoleto-0.0.1-SNAPSHOT.jar app.jar

ENV JAVA_OPTS="-Xmx128m -Xss256k -XX:+UseSerialGC -Djava.security.egd=file:/dev/./urandom -Dspring.jmx.enabled=false -Dspring.main.lazy-initialization=true"

EXPOSE 10000

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]