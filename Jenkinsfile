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
                        bat "& minikube -p ${MINIKUBE_PROFILE} docker-env --shell powershell | Invoke-Expression"
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
                                sh 'mvn clean install'
                                sh "docker build -t ${svc}:latest ."
                            } else {
                                bat 'mvn clean install'
                                bat "docker build -t ${svc}:latest ."
                            }
                        }
                    }
                }
            }
        }

        stage('deploy infrastructure'){
            steps{
                script{
                    echo "start app keycloak"
                    dir("helm-charts/keycloak") {
                            if (isUnix()) {
                                sh "kubectl create configmap keycloak-realm --from-file=realm-export.json"
                                sh "kubectl apply -f keycloak-deployment.yaml"
                            } else {
                                bat "kubectl create configmap keycloak-realm --from-file=realm-export.json"
                                bat "kubectl apply -f keycloak-deployment.yaml"
                            }
                        }

                    echo "start app postgres"
                    dir("helm-charts/postgres") {
                            if (isUnix()) {
                                sh "kubectl apply -f postgres-deployment.yaml"
                            } else {
                                bat "kubectl apply -f postgres-deployment.yaml"
                            }
                        }

                    echo "start app consul"
                    dir("helm-charts/consul") {
                            if (isUnix()) {
                                sh "kubectl apply -f consul.yaml"
                            } else {
                                bat "kubectl apply -f consul.yaml"
                            }
                        }
                    if (isUnix()) {
                        sh "kubectl rollout status deployment/consul --timeout=120s || true"
                    } else {
                        bat "kubectl rollout status deployment/consul --timeout=120s || true"
                    }

                    echo "load microservice configs into consul"
                    dir("consul") {
                        if (isUnix()) {
                            sh '''
                            CONSUL_POD=$(kubectl get pod -l app=consul -o jsonpath="{.items[0].metadata.name}")

                            for file in *.yaml; do
                                SERVICE_NAME=$(basename "$file" .yaml)
                                echo "Uploading config for $SERVICE_NAME"
                                kubectl exec -i $CONSUL_POD -- \
                                consul kv put config/$SERVICE_NAME @${file}
                            done
                            '''
                        } else {
                            bat '''
                            for %%f in (*.yaml) do (
                                set "SERVICE_NAME=%%~nf"
                                echo Uploading config for %SERVICE_NAME%
                                for /f "usebackq tokens=*" %%p in (`kubectl get pod -l app=consul -o jsonpath="{.items[0].metadata.name}"`) do (
                                    kubectl exec -i %%p -- consul kv put config/%SERVICE_NAME% @%%f
                                )
                            )
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
    }

    post {
        always {
            echo 'Pipeline finished'
        }
    }
}
