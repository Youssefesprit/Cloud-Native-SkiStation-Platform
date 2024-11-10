def services = [
    'api-gateway',
    'discovery-service',
    'instructor-management-service',
    'course-management-service'
]
pipeline {
    agent any
    environment {
        SONARQUBE_URL = 'http://192.168.33.10:9000' 
        SONARQUBE_LOGIN = 'squ_4dd82af4db1c1abe137ad444765d758c680016ce'
    }

    stages {
        stage('GIT') {
            steps {
                git branch: 'Malek-Instrctor-Management-Service',
                credentialsId: 'github-credentials',
                url: 'https://github.com/Youssefesprit/5SIM1-G6-Microservices-Gestion-Station-Ski.git'
            }
        }
        stage('Maven Build') {
            steps {
                echo 'Building Java project with Maven'
                sh 'mvn clean compile'
            }
        }
       /*   stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh 'mvn sonar:sonar -Dsonar.login=$SONAR_TOKEN'
                }
            }
        }*/
        stage('Mockito/JUnit') {
            steps {
                echo 'Running tests for instructor-management-service...'
                dir('instructor-management-service') {
                     sh 'mvn test -Dtest=tn.esprit.spring.services.InstructorServicesImplTest'
                }
            }
        }
        stage('Deploy to Nexus') {
            steps {
                configFileProvider([configFile(fileId: 'edd23d9c-d8c9-471d-bf6b-b262d3b712af', variable: 'MAVEN_SETTINGS')]) {
                    sh 'mvn clean deploy -DskipTests -s $MAVEN_SETTINGS'
                }
            }
        }
      

       /* stage('Run Docker Compose') {
            steps {
                script {
                    echo "Running Docker Compose"
                    withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                        sh 'docker compose up -d'
                    }
                }
            }
        }
        */
         /* stage('Clean Existing Docker Images') {
            steps {
                script {
                    for (service in SERVICES.split(",")) {
                        def imageName = "${DOCKER_REPO}/${service}:1.0"
                        def existingImage = sh(script: "docker images -q ${imageName}", returnStdout: true).trim()
                        
                        if (existingImage) {
                            echo "Removing existing image: ${imageName}"
                            sh "docker rmi -f ${imageName}"
                            sh "docker image prune -f"  // Force prune to remove dangling images
                        } else {
                            echo "No existing image found for: ${imageName}"
                        }
                    }
                }
            }
        }*/
        
       /* stage('Build Docker Images') {
            steps {
                script {
                    for (service in SERVICES.split(",")) {
                        echo "Building Docker image for: ${service}"
                        sh "docker build -t ${service}:1.0 ./${service}"
                    }
                }
            }
        }
        
        stage('Docker Login') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'nexus-credentials', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                    sh "echo $DOCKER_PASSWORD | docker login $DOCKER_REPO --username $DOCKER_USERNAME --password-stdin"
                }
            }
        }
        
        stage('Push Docker Images to Nexus') {
            steps {
                script {
                    for (service in SERVICES.split(",")) {
                        echo "Pushing Docker image: ${service}"
                        sh "docker tag ${service}:1.0 ${DOCKER_REPO}/${service}:1.0"
                        sh "docker push ${DOCKER_REPO}/${service}:1.0"
                    }
                }
            }
        }*/
              /* stage('Build and Push Gateway Service Image') {
                    steps {
                        script {
                          withDockerRegistry(credentialsId: 'dockerhub'){
                            dir('api-gateway') { 
                                echo "Building Docker image"
                                sh 'ls -l target'
                                sh 'docker build -t maleklabidi/gateway-service:1.0 .'
                                echo "Pushing Docker image to Docker Hub"
                                sh '''
                                    docker push maleklabidi/gateway-service:1.0
                                '''
                            }
                            }
                        }
                    }
                }*/
        stage('Build Docker Images') {
            steps {
                script {
                    withDockerRegistry(credentialsId: 'dockerhub') {
                        for (service in services) {
                            dir(service) {
                                echo "Building Docker image for ${service}"
                                sh 'ls -l target'
                                sh "docker build -t maleklabidi/${service}:1.0 ."
                            }
                        }
                        echo "Building Docker images completed successfully"
                    }
                }
            }
        }
         stage('Push Docker Images to docker Hub') {
            steps {
                script {
                    withDockerRegistry(credentialsId: 'dockerhub') {
                        for (service in services) {
                            dir(service) {
                                echo "Pushing Docker image for ${service} to Docker Hub"
                                sh "docker push maleklabidi/${service}:1.0"
                            }
                        }
                        echo "Pushing Docker images to docker hub completed successfully"
                    }
                }
            }
        }
               /*  stage('Build and Push Gateway Service Imageto nexus') {
                    steps {
                        script {
                          withDockerRegistry(credentialsId: 'nexusCredentials', url: 'https://192.168.33.10:8082'){
                            dir('api-gateway') { 
                                echo "Building Docker image"
                                sh 'ls -l target'
                                sh 'docker build -t https://192.168.33.10:8082/repository/docker-hosted/gateway-service:1.0 .'
                                echo "Pushing Docker image to nexus"
                                sh '''
                                    docker push https://192.168.33.10:8082/repository/docker-hosted/gateway-service:1.0
                                '''
                            }
                            }
                        }
                    }
                }
                */
               
        stage('Run Docker Containers') {
            steps {
                echo "Running Docker Compose"
                sh 'docker compose up -d' 
            }
        }
    }
    post {
        success {
            cleanWs()
            echo 'Pipline builded successfully'
             emailext(
            to: 'melek.islem105@gmail.com',
           subject: "✅ Build Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
        body: """
            <html>
                <body>
                    <h2 style="color: #28a745;">Build Successful! 🎉</h2>
                    <p>Good news! The build <strong>${env.JOB_NAME} #${env.BUILD_NUMBER}</strong> completed successfully.</p>
                    <p>Details:</p>
                    <ul>
                        <li><strong>Job Name:</strong> ${env.JOB_NAME}</li>
                        <li><strong>Build Number:</strong> ${env.BUILD_NUMBER}</li>
                        <li><strong>Status:</strong> <span style="color: #28a745;">Success</span></li>
                    </ul>
                    <p>You can review the build details at the following link:</p>
                    <a href="${env.BUILD_URL}" style="color: #007bff;">View Build</a>
                </body>
            </html>
        """,
        mimeType: 'text/html'
        )
        }
        failure{
            cleanWs()
            echo 'Pipline failed .Please check logs for more information'
             emailext(
            to: 'melek.islem105@gmail.com',
             subject: "❌ Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
        body: """
            <html>
                <body>
                    <h2 style="color: #dc3545;">Build Failed! ❗</h2>
                    <p>The build <strong>${env.JOB_NAME} #${env.BUILD_NUMBER}</strong> has failed. Please check the logs for more information.</p>
                    <p>Details:</p>
                    <ul>
                        <li><strong>Job Name:</strong> ${env.JOB_NAME}</li>
                        <li><strong>Build Number:</strong> ${env.BUILD_NUMBER}</li>
                        <li><strong>Status:</strong> <span style="color: #dc3545;">Failure</span></li>
                    </ul>
                    <p>You can review the build details at the following link:</p>
                    <a href="${env.BUILD_URL}" style="color: #007bff;">View Build</a>
                </body>
            </html>
        """,
        mimeType: 'text/html'
        )
        }
 
    }
    
}
