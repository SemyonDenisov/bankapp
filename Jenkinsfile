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
                        powershell '''
                                        Write-Host "Configuring Docker to use Minikube's Docker daemon"
                                        minikube -p minikube docker-env --shell powershell | Invoke-Expression
                                    '''

                    }
                }
            }
        }

        stage('Detect Services') {
            steps {
                script {
                    def microservices = ["api-gateway","eureka","notifications-microservice"]//,"blocker-microservice","accounts-microservice","exchange-microservice","transfer-microservice","cash-microservice","exchange-generator-microservice","front-ui-microservice"]

                    def extraServices = ['eureka', 'api-gateway']
                    env.SERVICES = (microservices + extraServices).join(',')
                }
            }
        }

     stage('Build & Docker') {
    steps {
        script {
            def services = env.SERVICES.split(',')

            services.each { svc ->
                echo "Building service: ${svc}"

                dir("bankapp/${svc}") {
                    if (isUnix()) {
                        // Linux / macOS
                        sh """
                            echo "Configuring Docker to use Minikube"
                            eval \$(minikube -p ${MINIKUBE_PROFILE} docker-env)

                            echo "Building Maven project"
                            mvn clean install

                            echo "Building Docker image"
                            docker build -t ${svc}:latest .
                        """
                    } else {
                        // Windows
                        powershell """
                            Write-Host 'Configuring Docker to use Minikube'
                            minikube -p ${env.MINIKUBE_PROFILE} docker-env --shell powershell | Invoke-Expression

                            Write-Host 'Building Maven project'
                            mvn clean install

                            Write-Host 'Building Docker image'
                            docker build -t ${svc}:latest .
                        """
                        }
                    }
                }
            }
        }
    }

    stage('deploy infrastructure') {
        steps {
            script {
                // Keycloak
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

                // Postgres
                dir("helm-charts/postgres") {
                    if (isUnix()) {
                        sh 'kubectl apply -f postgres-deployment.yaml'
                    } else {
                        powershell 'kubectl apply -f postgres-deployment.yaml'
                    }
                }

                // Consul
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

                // Load microservice configs into Consul
                dir("consul") {
                    if (isUnix()) {
                        sh '''
                            CONSUL_POD=$(kubectl get pod -l app=consul -o jsonpath="{.items[0].metadata.name}")

                            for file in *.yaml; do
                                SERVICE_NAME=$(basename "$file" .yaml)
                                echo "Uploading config for $SERVICE_NAME"
                                kubectl exec -i $CONSUL_POD -- consul kv put config/$SERVICE_NAME @${file}
                            done
                        '''
                    } else {
                        powershell '''
                            $CONSUL_POD = kubectl get pod -l app=consul -o jsonpath="{.items[0].metadata.name}"
                            Get-ChildItem *.yaml | ForEach-Object {
                                $SERVICE_NAME = $_.BaseName
                                Write-Host "Uploading config for $SERVICE_NAME"
                                kubectl exec -i $CONSUL_POD -- consul kv put config/$SERVICE_NAME @($_.FullName)
                            }
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
