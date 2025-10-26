mvn clean install 
docker build -t transfer-microservice .
docker run -d -p 8086:8086 --network mynet --name transfer-microservice transfer-microservice
