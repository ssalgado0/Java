# EPCSD User Service

Small Spring Boot sample service used in the EPCSD exercises. This README explains how to build and
run the project and lists the default users included in the embedded SQL schema.

## Quick plan

- Build with Maven (Java 11)
- Run locally
- Default users table (passwords in plaintext commented in `src/main/resources/schema.sql`)

## Prerequisites

- Java 11 (JDK)
- Maven 3.6+
- PostgreSQL available if you want to run against an external DB (the project uses JDBC URL env
  vars)

## Build

From the project root:

```sh
mvn clean package
```

## Run

The application reads configuration from environment variables. Example using an external PostgreSQL
instance (replace values as needed):

```sh
set DB_HOST=localhost && set DB_PORT=5432 && set DB_NAME=userdb && set DB_USER=postgres && set DB_PASSWORD=postgres && set SERVER_PORT=8080 && mvn spring-boot:run
```

Or run the packaged jar (after `mvn package`):

```sh
set DB_HOST=localhost && set DB_PORT=5432 && set DB_NAME=userdb && set DB_USER=postgres && set DB_PASSWORD=postgres && set SERVER_PORT=8080 && java -jar target\user-0.0.1-SNAPSHOT.jar
```

Notes:

- The application properties use placeholders for DB and server values. See
  `src/main/resources/application.properties`.
- By default the JPA configuration in `application.properties` is set to `create-drop` and SQL
  initialization is enabled: the schema in `src/main/resources/schema.sql` will be executed on
  startup.

## Default users (from `src/main/resources/schema.sql`)

The `schema.sql` file contains the INSERTs that create default users. Passwords are stored hashed in
the database, but the plaintext passwords are present as comments next to the hashes in the file.
For convenience, here are the default users and their plaintext passwords (copied from the comments
in `schema.sql`):

|          Full name | Email             | Role  | Plaintext password |
|-------------------:|-------------------|:-----:|-------------------:|
|        Juan Palomo | juanpa@gmail.com  | USER  |              12345 |
|    Francisco Pérez | fperez@gmail.com  | USER  |              12345 |
| José Manuel García | jmgarcia@terra.es | USER  |              12345 |
|       Carles Vidal | vidal_c@gmail.com | ADMIN |              admin |
|      Nil Carbonell | neil@gmail.com    | ADMIN |              admin |

Location: [`src/main/resources/schema.sql`](./src/main/resources/schema.sql) — you can open that
file to see the exact INSERT statements and the commented plaintext passwords.

## API documentation

If the application starts successfully, the OpenAPI / Swagger UI provided by `springdoc-openapi` is
usually available at:

- http://localhost:8080/swagger-ui.html
- or http://localhost:8080/swagger-ui/index.html

(adjust hostname/port if you changed `SERVER_PORT`). Use the UI to discover available endpoints and
authentication details.

## Where to find more

- SQL schema and seeded users: `src/main/resources/schema.sql`
- Spring configuration: `src/main/resources/application.properties`
- Build: `pom.xml`