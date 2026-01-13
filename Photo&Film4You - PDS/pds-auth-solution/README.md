# Auth Service

## Overview

This repository contains a small Spring Boot microservice that provides authentication
functionality (login + JWT generation). The service exposes an `/auth/login` endpoint which
authenticates a user by delegating to an external User Service and, when successful, returns a
signed JWT.

## Key components

- `AuthRESTController` - REST controller exposing `/auth/login`.
- `AuthService` - business logic that validates credentials and generates tokens.
- `UserRepository` (REST implementation) - calls the external User Service to retrieve user data for
  login.
- `JwtTokenProvider` - signs JWT tokens used by the service.

## Technology

- Java (project configured for Java 25 in the POM)
- Spring Boot (Web + Security)
- JJWT for JWT generation
- MapStruct for mapping DTOs
- JUnit 5 + Mockito for unit tests
- Maven as the build system

## Build and run

From the project root:

- Build and run unit tests:

```sh
mvn test
```

- Run the application locally:

```sh
mvn spring-boot:run
```

- Build the jar:

```sh
mvn package
```

- Run the packaged jar:

```sh
java -jar target/auth-0.0.1-SNAPSHOT.jar
```

## Integration tests

Integration tests are configured to run with the Maven Failsafe plugin and follow the `*IT.java`
naming convention (so they run in the `integration-test`/`verify` phases). To run integration tests
and generate coverage for them (JaCoCo integration):

```sh
mvn verify
```

Notes about the integration tests in this project:

- Integration tests spin an embedded HTTP server to simulate the external User Service (the property
  `userService.login.url` is overridden during test runtime).
- The Maven Failsafe plugin is configured to include `**/*IT.java` tests.

## Testing conventions

All test method names follow a clear BDD-style naming convention in camelCase:

    givenX_whenY_thenZ

Examples used in the test suite:

- `givenUser_whenGenerateToken_thenContainsExpectedClaims`
- `givenUserExists_whenGenerateToken_thenReturnsToken`
- `givenAuthService_returnsToken_whenLogin_thenReturns200WithBody`

This makes it easy to understand the precondition, the action under test and the expected outcome.

## Configuration and environment

Configuration files live under `src/main/resources` and `src/test/resources`. Important runtime
properties for tests:

- `userService.login.url` - endpoint used by the `UserRepository` to authenticate users.
- `jwt.secret` - secret used to sign tokens (set in test properties for deterministic tests).
- `jwt.expiration` - token TTL in milliseconds.

If you run the server locally and want it to call a real User Service, set `userService.login.url`
appropriately.

## Environment variables

The application `src/main/resources/application.properties` uses the following environment
variables (with defaults where applicable):

- `USER_SERVICE_URL` (default: `user-service`) – hostname or DNS name of the User Service. Used
  together with `USER_SERVICE_PORT` to build `userService.login.url`.
- `USER_SERVICE_PORT` (no default) – port where the User Service listens. Combined with
  `USER_SERVICE_URL` to form the `userService.login.url` value.
- `JWT_SECRET` (default: `ltJvymCeSOtT3xEpPpB3AyXGKwFdQ1dB8oWDFP4mYUM=`) – base64-like secret used
  to sign JWT tokens. Override in production with a secure secret.
- `JWT_EXPIRATION` (default: `3600000`) – token lifetime in milliseconds (default 1 hour).
- `SERVER_PORT` (no default) – optional port for the application to bind to (if not set, Spring Boot
  uses the default port 8080 unless another is configured).

The `userService.login.url` property is assembled as:

```
http://${USER_SERVICE_URL:user-service}:${USER_SERVICE_PORT}/internal/users/login
```