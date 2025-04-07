FROM openjdk:17-jdk-alpine
COPY target/GestionPanne.jar app.jar
EXPOSE 1218
ENTRYPOINT ["java", "-jar", "app.jar"]