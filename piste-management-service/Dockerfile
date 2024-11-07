# Use the official OpenJDK image as a base image
FROM openjdk:17-jdk-alpine

# Set the working directory to /app
WORKDIR /app

# Copy the JAR file into the container
COPY target/piste-management-service-1.0.jar ppiste-management-service.jar

# Expose the port that the service will run on
EXPOSE 8087

# Run the application
ENTRYPOINT ["java", "-jar", "piste-management-service.jar"]