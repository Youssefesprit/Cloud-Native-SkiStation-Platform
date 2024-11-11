pipeline {
    agent any
     environment {
        services = 'discovery-service,api-gateway,registration-management-service'
         SONARQUBE_URL = 'http://192.168.192.131:9000'
         SONARQUBE_LOGIN = 'squ_c94f45eb2303e27808394cfa4282e4844707a3e3'   
        IMAGE_TAG = "${env.BUILD_NUMBER}-${new Date().format('yyyyMMddHHmm')}"

     }  
    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'SaadallahChaima_RegistrationManagement',
                    url: 'https://github.com/Youssefesprit/5SIM1-G6-Microservices-Gestion-Station-Ski.git',
                    credentialsId: 'git_token'
            }
        }
        stage('Compile Stage') {
            steps {
                echo 'Building with Maven...'
                sh 'mvn compile'
            }
        }
          stage('Run Tests') {
           steps {
               sh 'mvn test -Dtest=tn.esprit.spring.services.SkierServicesImplTest -pl skier-management-service'  // Run specific tests
           }
       }
        stage('MVN SONARQUBE') {
            steps {
               script {
                  withSonarQubeEnv('sonarqube') { // Utilisez le nom que vous avez configuré dans Jenkins
                  sh 'mvn sonar:sonar -Dsonar.projectKey=RegistrationManagement -Dsonar.host.url=$SONARQUBE_URL -Dsonar.login=$SONARQUBE_TOKEN'
                  echo "tester le projet statiquement"
            }
        }
    }
}
        stage('Deploy to Nexus') {        
            steps {                           
                sh 'mvn clean deploy -DskipTests'     
            }         
        }
        stage('Build Docker Images') {
            steps {
                script {
                    def serviceList = services.split(',')
                    withDockerRegistry(credentialsId: 'DockerToken') {
                        for (service in serviceList) {
                            dir(service) {
                                echo "Building Docker image for ${service}"
                                sh 'ls -l target'
                                sh "docker build -t chaimasaadallah/${service}:1.0 ."
                                //sh "docker build -t chaimasaadallah/${service}:${IMAGE_TAG} ."
                            }
                        }
                        echo "Building Docker images completed successfully"
                    }
                }
            }
        }
        stage('Push Docker Images to Docker Hub') {
            steps {
                script {
                    def serviceList = services.split(',')
                    withDockerRegistry(credentialsId: 'DockerToken') {
                        for (service in serviceList) {
                            dir(service) {
                                echo "Pushing Docker image for ${service} to Docker Hub"
                                sh "docker push chaimasaadallah/${service}:1.0"
                                 //sh "docker push chaimasaadallah/${service}:${IMAGE_TAG}"
                            }
                        }
                        echo "Pushing Docker images to Docker Hub completed successfully"
                    }
                }
            }
        }
  stage('Docker Compose') {
            steps {
                script {
                    echo "Starting Docker Compose for services: ${services}"
                    withDockerRegistry(credentialsId: 'DockerToken') {
                        // Lancer docker-compose seulement avec les services spécifiés
                        sh "docker compose up -d"
                        echo "taaadaaa"

                    }
                }
            }
        }
    }
      post {
        always {
            echo 'Pipeline execution completed.'
        }
        success {
            emailext(
                to: 'saadallahchaima58@gmail.com',
                subject: "✅ SUCCESS: ${env.JOB_NAME} Build #${env.BUILD_NUMBER}",
                body: """\
                    <p><strong>Build Successful!</strong></p>
                    <p><strong>Job:</strong> ${env.JOB_NAME}</p>
                    <p><strong>Build Number:</strong> ${env.BUILD_NUMBER}</p>
                    <p><strong>Duration:</strong> ${currentBuild.durationString}</p>
                    <p><strong>Services Built:</strong> ${services}</p>
                    <p>You can view the details of the build <a href="${env.BUILD_URL}">here</a>.</p>
                    <p>Thank you,<br>Your Jenkins Pipeline</p>
                """,
                mimeType: 'text/html'
            )
        }
        failure {
            emailext(
                to: 'saadallahchaima58@gmail.com',
                subject: "❌ FAILURE: ${env.JOB_NAME} Build #${env.BUILD_NUMBER}",
                body: """\
                    <p><strong>Build Failed!</strong></p>
                    <p><strong>Job:</strong> ${env.JOB_NAME}</p>
                    <p><strong>Build Number:</strong> ${env.BUILD_NUMBER}</p>
                    <p><strong>Failed Stage:</strong> ${currentBuild.currentResult}</p>
                    <p><strong>Duration:</strong> ${currentBuild.durationString}</p>
                    <p><strong>Error Logs:</strong></p>
                    <pre>${currentBuild.rawBuild.getLog(20).join("\n")}</pre>
                    <p>For more details, please <a href="${env.BUILD_URL}">check the full build logs</a>.</p>
                    <p>Thank you,<br>Your Jenkins Pipeline</p>
                """,
                mimeType: 'text/html'
            )
        }
    }
}
