pipeline {
    agent any 

    environment {
        SONARQUBE_URL = 'http://192.168.33.10:9000'
        SONARQUBE_LOGIN = 'sqa_9bb123fed38f8ff454c3be6531c3f0ff2ab1d5be'            
    }

    stages {
        stage('GIT') {
            steps {
                git branch: 'JouhaynaCheikh-GestionCourse',
                    credentialsId: 'Github-Credentials', 
                    url: 'https://github.com/Youssefesprit/5SIM1-G6-Microservices-Gestion-Station-Ski.git'
            }
        }

        stage('Compile Stage') {
            steps {
                sh 'mvn clean compile'  
            }
        }

        stage('Test Stage') {
            steps {
                // Run tests only for the specified services
                sh 'mvn test -Dtest=tn.esprit.spring.services.CourseServicesImplTest -pl course-management-service'  // Execute specific tests -DfailIfNoTests=false
            }
        }

        stage('MVN SONARQUBE') {
            steps {
                withSonarQubeEnv('sonarqube') {  
                    sh 'mvn sonar:sonar -Dsonar.projectKey=GestionCourseProject -Dsonar.host.url=$SONARQUBE_URL -Dsonar.login=$SONARQUBE_LOGIN'
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
                    def services = ['discovery-service', 'api-gateway', 'course-management-service']
                    withDockerRegistry(credentialsId: 'DockerHubCredentials') {
                        for (service in services) {
                            dir(service) {
                                echo "Building Docker image for ${service}"
                                sh 'ls -l target'
                                sh "docker build -t jouhaynacheikh/${service}:1.0 ."
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
                    def services = ['discovery-service', 'api-gateway', 'course-management-service']
                    withDockerRegistry(credentialsId: 'DockerHubCredentials') {
                        for (service in services) {
                            dir(service) {
                                echo "Pushing Docker image for ${service} to Docker Hub"
                                sh "docker push jouhaynacheikh/${service}:1.0"
                            }
                        }
                        echo "Pushing Docker images to Docker Hub completed successfully"
                    }
                }
            }
        }

        stage('Run Container') {
            steps {
                sh 'docker compose up -d'
            }
        }
    }

    post {
         success {
            // This will run only if the build was successful
            emailext(
                to: 'joouhaynacheikh@gmail.com',
                subject: "Build ${currentBuild.fullDisplayName} Success",
                body: "The build was successful! Check the details here: ${env.BUILD_URL}",
                attachLog: true
            )
        }
        
        failure {
            // This will run only if the build failed
            emailext(
                to: 'joouhaynacheikh@gmail.com',
                subject: "Build ${currentBuild.fullDisplayName} Failed",
                body: "The build has failed. Please check the logs for errors: ${env.BUILD_URL}",
                attachLog: true
            )
    }
}

}


