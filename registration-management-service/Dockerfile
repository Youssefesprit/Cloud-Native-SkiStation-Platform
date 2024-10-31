# Utiliser l'image Java 17 pour exécuter l'application
FROM openjdk:17-jdk-alpine

# Définir le répertoire de travail à l'intérieur du conteneur
WORKDIR /app

# Copier le fichier JAR généré par Maven dans le conteneur
COPY target/registration-managementi-service-1.0-SNAPSHOT.jar registration-managementi-service.jar

# Exposer le port 8086, car c'est le port où ton service écoutera
EXPOSE 8086

# Commande d'exécution du service
ENTRYPOINT ["java", "-jar", "registration-management-service.jar"]
