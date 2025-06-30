FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre AS runtime

WORKDIR /app
COPY --from=build /app/target/PhoneHashServer-1.0-jar-with-dependencies.jar app.jar

ENTRYPOINT ["/bin/bash", "-c", "\
  until echo > /dev/tcp/redis/6379; do \
    echo '⏳ Waiting for redis:6379...'; \
    sleep 1; \
  done; \
  echo 'Redis is ready to connect'; \
  exec java -jar app.jar \
"]