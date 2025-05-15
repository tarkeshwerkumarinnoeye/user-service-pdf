# User Service Deployment

This directory contains Docker configuration files for building and running the User Service application.

## Prerequisites

- Docker and Docker Compose installed on your system

## Building and Running with Docker

### Using Docker Compose (Recommended)

From the root directory of the project, run:

```bash
docker-compose -f deployment/docker-compose.yml up -d
```

This will build the Docker image and start the container in detached mode.

### Using Docker Directly

To build the Docker image:

```bash
docker build -t user-service:latest -f deployment/Dockerfile .
```

To run the container:

```bash
docker run -p 8080:8080 -d user-service:latest
```

## Accessing the Application

Once the container is running, the application will be available at:

```
http://localhost:8080
```

## Health Check

The Docker container includes a health check that verifies the application's health via Spring Boot Actuator at:

```
http://localhost:8080/actuator/health
```

This endpoint provides detailed health information about the application and its dependencies.
