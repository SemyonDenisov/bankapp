FROM openjdk:21-ea-26-jdk-oracle

ENV TZ=Europe/Samara

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "app.jar"]
