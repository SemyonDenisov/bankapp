mvn clean install 
docker build -t front-ui-microservice .
docker run -d -p 8089:8089 --network mynet --name front-ui-microservice front-ui-microservice
