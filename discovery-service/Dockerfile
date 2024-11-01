# Use the official OpenJDK image as a base image
FROM openjdk:17-jdk-alpine

# Set the working directory to /app
WORKDIR /app

# Copy the JAR file into the container
COPY target/*.jar app.jar

# Expose the port that the service will run on
EXPOSE 8761

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
