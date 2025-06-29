FROM eclipse-temurin:17-jdk

WORKDIR /app
COPY target/PhoneHashServer-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]