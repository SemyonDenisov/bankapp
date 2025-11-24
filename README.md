для запуска: запустить Jenkinsfile

логин/пароль grafana: 1/1 

дашборды лежат в helm-charts\grafana\dashboards


port-forwards для просмотра:

приложение - kubectl port-forward service/front-ui-microservice 8089:8089
kibana - kubectl port-forward service/kibana 5601:5601
elasticsearch - kubectl port-forward service/elasticsearch 9200:9200
prometheus - kubectl port-forward service/prometheus  9090:9090
grafana - kubectl port-forward service/grafana 3000:3000
zipkin - kubectl port-forward service/zipkin 9411:9411
logstash - kubectl port-forward service/logstash  5044:5044


