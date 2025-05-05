# Personal Data Management (DPD) Application

A full-stack application for managing personal user data, built with Next.js (TypeScript, Shadcn UI) for the frontend and Spring Boot (Java, Hibernate, PostgreSQL, Liquibase) for the backend, all containerized with Docker.

## Prerequisites

*   **Docker:** Ensure Docker Desktop or Docker Engine is installed and running. ([Install Docker](https://docs.docker.com/get-docker/))
*   **Docker Compose:** Usually included with Docker Desktop. If not, install it separately. ([Install Docker Compose](https://docs.docker.com/compose/install/))
*   **Git:** For cloning the repository (if applicable).
*   **`.env` file:** A `.env` file is required in the project root directory for database credentials.

## Environment Setup

1.  **Clone the repository (if you haven't already):**
    ```bash
    git clone <repository-url>
    cd dpd # Or your project directory name
    ```

2.  **Create a `.env` file** in the root directory of the project (alongside `docker-compose.yml`) with the following content, replacing the placeholder values with your desired database credentials:

    ```dotenv
    # .env file content

    # PostgreSQL Credentials
    DATABASE_USERNAME=your_db_user
    DATABASE_PASSWORD=your_strong_db_password

    # Database URL for Spring Boot (uses service name 'postgres' from docker-compose)
    DATABASE_URL=jdbc:postgresql://postgres:5432/dpd
    ```
    *Note: The `POSTGRES_DB` name (`dpd`) is set directly in `docker-compose.yml` and should match the database name in `DATABASE_URL`.*

## Running the Application

1.  **Build and Run Containers:**
    Open a terminal in the project root directory (where `docker-compose.yml` is located) and run:
    ```bash
    docker-compose up --build -d
    ```
    *   `--build`: Forces Docker Compose to build the images (necessary after code changes).
    *   `-d`: Runs the containers in detached mode (in the background).

2.  **Wait for Services:** Allow some time for the database to initialize and the backend/frontend applications to start up. The backend performs database migrations via Liquibase on startup.

## Accessing the Application

*   **Frontend:** Open your web browser and navigate to [http://localhost:3000](http://localhost:3000)
*   **Backend API Base:** The API is available at `http://localhost:8088/api/v1`
*   **Backend Swagger UI (API Docs):** Navigate to [http://localhost:8088/swagger-ui.html](http://localhost:8088/swagger-ui.html) to explore the REST API endpoints.

## Stopping the Application

To stop the running containers, execute the following command in the project root directory:
```bash
docker-compose down
```
To stop and remove the volumes (including database data), use:
```bash
docker-compose down -v
