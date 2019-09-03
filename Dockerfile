FROM openjdk:8-alpine
COPY target/innovation-spoofing-1.0-SNAPSHOT.jar /app.jar
CMD ["java", "-jar", "/app.jar", "server"]

EXPOSE 8080/tcp
EXPOSE 8081/tcp
