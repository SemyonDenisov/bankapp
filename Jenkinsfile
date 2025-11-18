pipeline {
    agent any

    environment {
        MINIKUBE_PROFILE = "minikube"
    }

    stages {

        stage('Setup Docker Env for Minikube') {
        steps {
            script {
                echo "Configuring Docker to use Minikube's Docker daemon"
                if (isUnix()) {
                    sh "eval \$(minikube -p ${MINIKUBE_PROFILE} docker-env)"
                } else {
                    powershell """
                        Write-Host "Configuring Docker to use Minikube's Docker daemon"
                        & minikube -p ${env.MINIKUBE_PROFILE} docker-env | Invoke-Expression
                    """
                }
            }
        }
        }

        stage('Detect Services') {
             steps { 
                script { 
                    def microservices = ["api-gateway","eureka","accounts-microservice","blocker-microservice","exchange-microservice","exchange-generator-microservice","notifications-microservice","transfer-microservice","cash-microservice","front-ui-microservice"] 
                    env.SERVICES = (microservices).join(',') 
                    } 
                }

        }

        stage('Run Tests') {
            steps {
                script {
                    def services = env.SERVICES.split(',')

                    services.each { svc ->
                        echo "Running tests for: ${svc}"

                        dir("${svc}") {
                            if (isUnix()) {
                                sh "mvn clean test"
                            } else {
                                powershell "mvn clean test"
                            }
                        }
                    }
                }
            }
        }


        stage('Build & Docker') {
            steps {
                script {
                    def services = env.SERVICES.split(',')

                    services.each { svc ->
                        echo "Building service: ${svc}"

                        dir("${svc}") {
                            if (isUnix()) {
                                sh """
                                    echo "Configuring Docker to use Minikube"
                                    eval \$(minikube -p ${MINIKUBE_PROFILE} docker-env)

                                    echo "Building Maven project"
                                    mvn clean install -DskipTests

                                    echo "Building Docker image"
                                    docker build -t ${svc}:latest .
                                """
                            } else {
                                powershell """
                                    Write-Host 'Configuring Docker to use Minikube'
                                    minikube -p ${env.MINIKUBE_PROFILE} docker-env --shell powershell | Invoke-Expression

                                    Write-Host 'Building Maven project'
                                    mvn clean install -DskipTests

                                    Write-Host 'Building Docker image'
                                    docker build -t ${svc}:latest .
                                """
                            }
                        }
                    }
                }
            }
        }

        stage('Deploy Infrastructure') {
            steps {
                script {
                    dir("helm-charts/keycloak") {
                        if (isUnix()) {
                            sh '''
                                kubectl create configmap keycloak-realm --from-file=realm-export.json || true
                                kubectl apply -f keycloak-deployment.yaml
                            '''
                        } else {
                            powershell '''
                                kubectl create configmap keycloak-realm --from-file=realm-export.json -o yaml --dry-run=client | kubectl apply -f -
                                kubectl apply -f keycloak-deployment.yaml
                            '''
                        }
                    }

                    dir("helm-charts/postgres") {
                        if (isUnix()) {
                            sh 'kubectl apply -f postgres-deployment.yaml'
                        } else {
                            powershell 'kubectl apply -f postgres-deployment.yaml'
                        }
                    }

                    dir("helm-charts/consul") {
                        if (isUnix()) {
                            sh '''
                                kubectl apply -f consul.yaml
                                kubectl rollout status deployment/consul --timeout=120s || true
                            '''
                        } else {
                            powershell '''
                                kubectl apply -f consul.yaml
                                kubectl rollout status deployment/consul --timeout=120s
                            '''
                        }
                    }

                    dir("helm-charts/kafka") {
                        if (isUnix()) {
                            sh '''
                                echo "Deploying Kafka via Helm chart"
                                helm upgrade --install kafka .
                            '''
                        } else {
                            powershell '''
                                Write-Host "Deploying Kafka via Helm chart"
                                helm upgrade --install kafka .
                            '''
                        }
                    }

                   dir("consul") {
                        if (isUnix()) {
                            sh '''
                                CONSUL_POD=$(kubectl get pod -l app=consul -o jsonpath="{.items[0].metadata.name}")

                                for file in *.yaml; do
                                    SERVICE_NAME=$(basename "$file" .yaml)
                                    echo "Uploading config for $SERVICE_NAME"
                                    kubectl exec -i $CONSUL_POD -- consul kv put config/$SERVICE_NAME/data @${file}
                                done
                            '''
                        } else {
                            powershell '''
                                $CONSUL_POD = kubectl get pod -l app=consul -o jsonpath="{.items[0].metadata.name}"

                                Get-ChildItem *.yaml | ForEach-Object {
                                    $SERVICE_NAME = $_.BaseName
                                    Write-Host "Uploading config for $SERVICE_NAME"
                                    Get-Content $_.FullName | kubectl exec -i $CONSUL_POD -- consul kv put config/$SERVICE_NAME/data -
                                }
                            '''
                        }
                    }

                }
            }
        }

        stage('Deploy via Helm') {
            steps {
                script {
                    def services = env.SERVICES.split(',')

                    services.each { svc ->
                        echo "Deploying service via Helm: ${svc}"

                        dir("helm-charts/${svc}") {
                            if (isUnix()) {
                                sh "helm upgrade --install ${svc} ."
                            } else {
                                bat "helm upgrade --install ${svc} ."
                            }
                        }
                    }
                }
            }
        }

        stage('Start Port Forward') {
            steps {
                script {
                    if (isUnix()) {
                        sh '''
                            kubectl rollout status deployment/front-ui-microservice --timeout=180s
                            nohup kubectl port-forward service/front-ui-microservice 8089:8089 >/dev/null 2>&1 &
                        '''
                    } else {
                        powershell '''
                            kubectl rollout status deployment/front-ui-microservice --timeout=180s
                            Start-Process -NoNewWindow kubectl "port-forward service/front-ui-microservice 8089:8089"
                        '''
                    }
                }
            }
        }

    }

    post {
        always {
            echo 'Pipeline finished'
        }
    }

}
