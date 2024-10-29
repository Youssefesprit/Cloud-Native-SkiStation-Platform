# Use the official OpenJDK image as a base image
FROM openjdk:17-jdk-alpine

# Set the working directory to /app
WORKDIR /app

# Copy the JAR file into the container
ADD target/registration-managementi-service-0.0.1-SNAPSHOT.jar registration-managementi-service-0.0.1-SNAPSHOT.jar

# Expose the port that the service will run on
EXPOSE 8086

# Run the application
ENTRYPOINT ["java", "-jar", "registration-managementi-service-0.0.1-SNAPSHOT.jar"]