mvn clean install 
docker build -t eureka .
docker run -d -p 8761:8761 --network mynet --name eureka eureka    
