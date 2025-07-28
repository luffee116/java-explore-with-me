FROM eclipse-temurin:21-jre-jammy
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} App.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]