mvn clean install 
docker build -t exchange-generator-microservice .
docker run -d -p 8084:8084 --network mynet --name exchange-generator-microservice exchange-generator-microservice
