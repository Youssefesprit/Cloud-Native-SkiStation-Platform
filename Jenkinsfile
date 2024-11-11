pipeline { 

    agent any 

    environment { 
        // Repository and branch information 
        REPOURL = 'https://github.com/Youssefesprit/5SIM1-G6-Microservices-Gestion-Station-Ski.git' 
        BRANCHNAME = 'Youssef-Farhat-5SIM1-G6' 
        DIRNAME = '5SIM1-G6-Microservices-Gestion-Station-Ski' 

        // SonarQube configuration 
        SONARQUBE_URL = 'http://192.168.50.4:9000' 
        SONARQUBE_LOGIN ='squ_0065b177545f225dd34dc300c908ced81c71e1e1' 

        // Docker-hosted repository 
        DOCKER_REPO = '192.168.50.4:8082' 

        // Specify services as a comma-separated string 
        SERVICES = 'discovery-service,api-gateway,subscription-management-service,skier-management-service' 
    }     


    
    stages { 
        stage('Checkout Code') { 
            steps { 
                script { 
                    if (fileExists(DIRNAME)) { 
                        dir(DIRNAME) { 
                            sh "git checkout ${BRANCHNAME}" 
                            sh "git pull origin ${BRANCHNAME}" 
                        } 
                    } else { 
                       echo 'Cloning the repository' 
                        git branch: BRANCHNAME, 
                           credentialsId: 'github-credentials',  
                            url: REPOURL 
                    } 
                } 
            } 
        } 


        
        stage('Compile Specified Services') { 
            steps { 
                script { 
                        sh "mvn clean compile" 
                } 
            } 
        } 

        
        stage('Run Tests for Specified Service') { 
            steps { 
                sh 'mvn test -Dtest=tn.esprit.spring.services.SkierServicesImplTest -pl skier-management-service'   
            } 
        } 

        
          stage('SonarQube Analysis') { 
            steps { 
                sh 'mvn -X sonar:sonar -Dsonar.projectKey=skierManagement -Dsonar.host.url=$SONARQUBE_URL -Dsonar.login=$SONARQUBE_LOGIN' 
            } 
        } 

        
        stage('Build and Deploy to Maven Repository for Specified Services') { 
            steps { 
                script { 
                    configFileProvider([configFile(fileId: '7e5f31bd-7f72-47d9-96ff-15056b570786', variable: 'mavensettings')]) { 
                        SERVICES.split(',').each { service -> 
                            echo "Building and deploying ${service} to Maven Repository" 
                            sh "mvn -s $mavensettings deploy -pl ${service} -am -DskipTests=true" 
                        } 
                    } 
                } 
            } 
        } 
        stage('Build Docker Images') { 
            steps { 
                script { 
                    SERVICES.split(',').each { service -> 
                        echo "Building Docker image for: ${service}" 
                        sh "docker build --cache-from ${DOCKER_REPO}/${service}:1.0 -t ${DOCKER_REPO}/${service}:1.0 ./${service}" 
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

          stage('Push Docker Images to Docker Repository') { 
            steps { 
                script { 
                    SERVICES.split(',').each { service -> 
                        echo "Pushing Docker image: ${DOCKER_REPO}/${service}:1.0 to Docker repository" 
                        sh "docker push ${DOCKER_REPO}/${service}:1.0" 
                    } 
                } 
            } 
        } 

        stage('Run Docker Containers') { 
            steps { 
                sh 'docker-compose up -d'   
            } 
        } 
    } 

  post { 
    always { 
        sh "docker image prune -f"
        cleanWs()
    }
     success { 
        emailext( 
            to: 'yousseffarhat818@gmail.com', 
            subject: "✅ Stage Success: Build Completed", 
            body: """
                <html>
                    <body style="font-family: Arial, sans-serif; color: #333;">
                        <h2 style="color: #4CAF50;">✅ Build Stage Success</h2>
                        <p>The <strong>Build</strong> stage in <strong>${env.JOB_NAME}</strong> <em>#${env.BUILD_NUMBER}</em> has completed successfully.</p>
                        <p>Great work, team!</p>
                        <hr style="border: 1px solid #4CAF50;">
                        <p style="font-size: 12px; color: #888;">This is an automated message from Jenkins.</p>
                    </body>
                </html>
            """,
            mimeType: 'text/html'
        )
    } 
    failure { 
        emailext( 
            to: 'yousseffarhat818@gmail.com', 
            subject: "❌ Stage Failed: Build Stage Error", 
            body: """
                <html>
                    <body style="font-family: Arial, sans-serif; color: #333;">
                        <h2 style="color: #FF5722;">❌ Build Stage Failed</h2>
                        <p>The <strong>Build</strong> stage in <strong>${env.JOB_NAME}</strong> <em>#${env.BUILD_NUMBER}</em> has failed.</p>
                        <p>Please check the <a href="${env.BUILD_URL}" style="color: #FF5722;">build details</a> for more information and troubleshooting steps.</p>
                        <hr style="border: 1px solid #FF5722;">
                        <p style="font-size: 12px; color: #888;">This is an automated message from Jenkins.</p>
                    </body>
                </html>
            """,
            mimeType: 'text/html'
        )
    }
}


} 
