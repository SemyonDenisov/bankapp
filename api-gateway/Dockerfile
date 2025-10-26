FROM openjdk:21

ENV TZ=Europe/Samara

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8088

ENTRYPOINT ["java", "-jar", "app.jar"]
