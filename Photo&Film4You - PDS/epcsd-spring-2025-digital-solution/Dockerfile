FROM eclipse-temurin:11-jre-alpine
WORKDIR /app
COPY target/digital-*.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]