FROM openjdk:17-jdk-alpine
COPY target/GestionPanne.jar app.jar
EXPOSE 1812
ENTRYPOINT ["java", "-jar", "app.jar"]