# Photo&Film4You - Main Docker Compose

## About this project

This repository contains the top-level Docker Compose and helper compose files used to start the complete 
**Photo&Film4You**. The main compose is intended to run the full system by pulling the microservice Docker images 
from the GitLab image registry.

The repository also includes development-specific compose files under the [`dev/`](./dev) folder that bring up just the
infrastructure components (databases, Kafka, Adminer) or the full backend stack for frontend development.

## Prerequisites

- Docker and Docker Compose installed. On Windows, Docker Desktop is recommended (it includes Compose).
- Internet access to download images from the configured container registry (or a local registry if you mirror images).

## How to run

The main [`docker-compose.yml`](./docker-compose.yml) brings up the full application by pulling pre-built images for the
microservices from the GitLab registry. Environment variables control ports, image tags and other runtime settings.

- By default, the compose uses the variables from `.env` located next to `docker-compose.yml`.
- To run the environment in production mode, use the `.env.prod` file instead.
- To create a custom environment, copy `.env.template` to `.env` and edit the variables you need.

### Examples:

- Login intro  GitLab container registry (replace with your username and personal access token or password):

  ```sh
  docker login registry.gitlab.com -u YOUR_USERNAME -p YOUR_PERSONAL_ACCESS_TOKEN
  ```

- Download the latest image versions and start the full stack:

  ```sh
  docker compose pull
  docker compose up -d --force-recreate
  ```

- Start with the default `.env`:

  ```sh
  docker compose up -d
  ```

- Start using the production env file (explicitly point to it):

  ```sh
  docker compose --env-file .env.prod up -d
  ```

- Build and recreate (if you change local images or want a forced recreation):

  ```sh
  docker compose up -d --build --force-recreate
  ```

## Development mode (dev/)

For development you can use the compose files under the `dev/` directory. They are intended to make developing and
testing the microservices easier.

- [`dev/docker-compose.yml`](./dev/docker-compose.yml) — brings up the basic infrastructure required by the 
microservices (PostgreSQL databases, Kafka, Zookeeper, Adminer). Use this when you want to run individual microservices
locally but depend on the shared infrastructure.

- `dev/docker-compose-backend.yml` — brings up the full backend stack (all microservices plus infrastructure). This is
useful when frontend development needs the entire backend available without pulling images from the registry.

### Examples (from repository root):

  ```sh
  cd dev
  docker compose up -d
  ```

Or directly from the root referencing the file:

  ```sh
  docker compose -f dev/docker-compose-backend.yml up -d
  ```

## Ports and services

The compose files use non-default ports to avoid conflicts. Default ports used by the setup (can be changed through the
environment files):

- 22181 - Kafka (Zookeeper)
- 19092 - Kafka (broker)
- 54320, 54321, 54322 - PostgreSQL instances (productdb, userdb, digitaldb)
- 18080 - Adminer (DB web client)
- 18081 - ProductCatalog service
- 18082 - User service
- 18083 - Notification service
- 18084 - Digital service
- 18085 - Auth service
- 8080 - Gateway service
- 4200 - Frontend service (Angular)

Frontend application should be available at: http://localhost:4200/, together with Adminer (web DB client) at: http://localhost:18080/.

### Example DB connection credentials (default in the supplied environment files):

- productdb: user=product, password=product, database=product
- userdb: user=user, password=user, database=user
- digitaldb: user=digital, password=digital, database=digital

## How to use the application

### Accessing the frontend

Once the full stack is running, access the frontend application at: http://localhost:4200/

### Default users

The system automatically creates some default users in the user service database on startup.
For convenience, here are the default users and their plaintext passwords:

|          Full name | Email               |   Role   | Plaintext password |
|-------------------:|---------------------|:--------:|-------------------:|
|        Juan Palomo | juanpa@gmail.com    |   USER   |              12345 |
|    Francisco Pérez | fperez@gmail.com    |   USER   |              12345 |
| José Manuel García | jmgarcia@terra.es   |   USER   |              12345 |
|       Carles Vidal | vidal_c@gmail.com   |  ADMIN   |              admin |
|      Nil Carbonell | neil@gmail.com      |  ADMIN   |              admin |
|    Sistema externo | externo@externo.com | EXTERNAL |              12345 |

## Links and resources

- Docker: https://www.docker.com/
- Docker Compose docs: https://docs.docker.com/compose/
- Spring / Spring Boot: https://spring.io/
- Kafka: https://kafka.apache.org/
- PostgreSQL: https://www.postgresql.org/
- Angular: https://angular.io/

## Release Notes

### Version `0.0.3` (Sprint 2)

- New functionalities related to user profile management:
    - Create new user profile.
    - Edit user personal information.
    - View user profile information.
    - Change user password.
    - Delete user profile.
- New functionalities related to availability alerts creation.
    - Create availability alerts.
    - Manage availability alerts: View and delete available alerts.
- New functionalities related to digital content management.
    - Create digital content sessions.
    - Consult a digital content session.
    - Edit a digital content session.
    - Add a new item to a digital content session.
    - Edit an item of a digital content session.
    - Delete an item from a digital content session.
- New functionalities related to product catalog management.
    - Create new categories.
    - Update product information.
    - Delete a product.
- New functionalities related to digital content review.
    - View notifications of potential intellectual property conflict.
    - Close notifications.
