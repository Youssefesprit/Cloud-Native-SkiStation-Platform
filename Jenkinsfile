pipeline {
    agent any 
    environment {
        SONARQUBE_URL = 'http://192.168.40.136:9000' 
        SONARQUBE_LOGIN = 'squ_abea769e87a80301e1f974de4d868418a752a917'            
    }


    
    stages {
         stage('GIT') {
            steps {
                git branch: 'Chaima-Piste-Management-Service',
                    credentialsId: 'Github-Credentials', 
                    url: 'https://github.com/Youssefesprit/5SIM1-G6-Microservices-Gestion-Station-Ski.git'
            }
        }
       stage('Compile Stage') {
            steps {
                // Compile only the specified modules
                sh 'mvn clean compile'
            }
        }
        stage('Test Stage') {
            steps {
                // Run tests only for the specified services
                sh 'mvn test -Dtest=tn.esprit.spring.services.PisteServicesImplTest -pl piste-management-service'
            }
        }
        
        // Uncomment the following stage if you want to enable SonarQube analysis
     
        stage('MVN SONARQUBE') {
            steps {
                withSonarQubeEnv('sonarqube') {  
                    sh 'mvn sonar:sonar -Dsonar.projectKey=PisteManagementService -Dsonar.host.url=$SONARQUBE_URL -Dsonar.login=$SONARQUBE_LOGIN'
                }
            }
        }
     
  
        stage('Deploy to Nexus') {
            steps {
                script {
                    def services = ['discovery-service', 'api-gateway', 'piste-management-service']
                    services.each { service ->
                        dir(service) { // Change directory to each service
                            echo "Deploying ${service} to Nexus"
                            withCredentials([usernamePassword(credentialsId: 'Nexus', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                                sh 'mvn clean deploy -DskipTests -Dnexus.user=$NEXUS_USER -Dnexus.password=$NEXUS_PASS'
                            }
                        }
                    }
                }
            }
        }

        stage('Build and Push Docker Images') {
            steps {
                script {
                    def services = ['discovery-service', 'api-gateway', 'piste-management-service']
                    withCredentials([usernamePassword(credentialsId: 'DockerHub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        services.each { service ->
                            // Build Docker image with error handling and no cache for better debugging
                            sh """
                                docker build --no-cache -t chaimaaa20/${service}:1.0 ./${service} || { echo 'Build failed for ${service}'; exit 1; }
                            """
 
                            // Docker login with error handling
                            sh """
                                echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin || { echo 'Docker login failed'; exit 1; }
                            """
 
                            // Push Docker image with error handling
                            sh """
                                docker push chaimaaa20/${service}:1.0 || { echo 'Push failed for ${service}'; exit 1; }
                            """
                        }
                    }
                }
            }
        }
 
        stage('Run Docker Compose') {
            steps {
                echo 'Starting services with Docker Compose...'
                sh 'docker compose up -d'
            }
        }
    }
    
    post {         
        success {             
            emailext(
                to: 'chaima.eljed@esprit.tn',                 
                subject: "Stage Success: Build stage completed",                 
                body: "The 'Build' stage in ${env.JOB_NAME} #${env.BUILD_NUMBER} completed successfully."             
            )
        }         
        failure {             
            emailext(
                to: 'chaima.eljed@esprit.tn',                 
                subject: "Stage Failed: Build stage failed",                 
                body: "The 'Build' stage in ${env.JOB_NAME} #${env.BUILD_NUMBER} has failed. Check ${env.BUILD_URL}"             
            )
        }
    }
}
