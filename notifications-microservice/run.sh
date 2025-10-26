mvn clean install 
docker build -t notifications-microservice .
docker run -d -p 8083:8083 --network mynet --name notifications-microservice notifications-microservice
