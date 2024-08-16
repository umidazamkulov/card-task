# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the project files to the container
COPY . .

# Package the application
RUN ./mvnw package

# Run the application
CMD ["java", "-jar", "target/card_task-0.0.1-SNAPSHOT.jar"]
