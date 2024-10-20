# Use the official OpenJDK image as a base image
FROM openjdk:17-jdk-alpine

# Set the working directory to /app
WORKDIR /app

# Copy the JAR file into the container
ADD target/piste-management-service-0.0.1-SNAPSHOT.jar ipiste-management-service-0.0.1-SNAPSHOT.jar

# Expose the port that the service will run on
EXPOSE 8089

# Run the application
ENTRYPOINT ["java", "-jar", "piste-management-service-0.0.1-SNAPSHOT.jar"]