mvn clean install 
docker build -t api-gateway .
docker run -d -p 8088:8088 --network mynet --name api-gateway api-gateway
