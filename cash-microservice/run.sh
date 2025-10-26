mvn clean install 
docker build -t cash-microservice .
docker run -d -p 8082:8082 --network mynet --name cash-microservice cash-microservice
