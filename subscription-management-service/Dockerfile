# Use the official OpenJDK image as a base image
FROM openjdk:17-jdk-alpine

# Set the working directory to /app
WORKDIR /app

# Copy the JAR file into the container
ADD target/subscription-management-service-1.0.jar subscription-management-service.jar

# Expose the port that the service will run on
EXPOSE 8085

# Run the application
ENTRYPOINT ["java", "-jar", "subscription-management-service.jar"]
