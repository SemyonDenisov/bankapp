mvn clean install 
docker build -t blocker-microservice .
docker run -d -p 8087:8087 --network mynet --name blocker-microservice blocker-microservice
