# DPD Docker Compose Application

This is a Docker Compose application that consists of multiple services.

## Getting Started

To run this application, you need Docker and Docker Compose installed on your machine.

### Starting the Application

1. Clone this repository:
   ```bash
   git clone https://github.com/your/repository.git
   cd your-project-directory
2. Build and start the Docker containers:
   ```bash
   docker compose up
3. Once the containers are up and running, you can access the application at:
- Main App: http://localhost:8088
- OpenAI or Swagger: http://localhost:8088/api/v1/swagger-ui/index.html
4. Stopping the Application:
To stop the application and shut down the Docker containers, press Ctrl + C in the terminal where you started the containers. Alternatively, you can run:
   ```bash
   docker compose down
