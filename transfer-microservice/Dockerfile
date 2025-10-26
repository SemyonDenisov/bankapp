FROM openjdk:21

ENV TZ=Europe/Samara

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "app.jar"]
