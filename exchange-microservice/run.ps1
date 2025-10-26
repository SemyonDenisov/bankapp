mvn clean install 
docker build -t exchange-microservice .
docker run -d -p 8085:8085 --network mynet --name exchange-microservice exchange-microservice
