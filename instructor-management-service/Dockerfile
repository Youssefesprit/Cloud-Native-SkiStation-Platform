# Use the official OpenJDK image as a base image
FROM openjdk:17-jdk-alpine

# Set the working directory to /app
WORKDIR /app

# Copy the JAR file into the container
ADD target/instructor-management-service-0.0.1-SNAPSHOT.jar instructor-management-service-0.0.1-SNAPSHOT.jar

# Expose the port that the service will run on
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "instructor-management-service-0.0.1-SNAPSHOT.jar"]