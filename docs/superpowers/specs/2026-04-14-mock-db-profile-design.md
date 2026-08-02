# Mock DB Profile Design (H2)

## Goal
Provide a dedicated `mock` profile that runs the app without connecting to the real Postgres database, so Swagger UI can be verified locally and safely.

## Non-Goals
- Replace or modify the default Postgres configuration.
- Add production database changes.
- Introduce Docker or external dependencies.

## Approach
- Add H2 as a runtime dependency so the application can boot under a mock profile.
- Add `application-mock.properties` to configure an in-memory H2 database.
- Keep `application.properties` unchanged (Postgres remains the default profile).

## Configuration Details
`src/main/resources/application-mock.properties`:
- Use H2 in-memory datasource (MODE=PostgreSQL for compatibility).
- Set `spring.jpa.hibernate.ddl-auto=create-drop` for ephemeral schema.
- Disable SQL logging unless needed.

## Usage
Run the app locally with:

```
SPRING_PROFILES_ACTIVE=mock ./mvnw spring-boot:run
```

Swagger UI should be available at `http://localhost:8080/swagger-ui.html`.

## Risks / Tradeoffs
- H2 is not fully Postgres-compatible; suitable for Swagger UI and basic local testing, not for production parity.

## Verification
- App boots with `SPRING_PROFILES_ACTIVE=mock` without contacting the real DB.
- Swagger UI loads and lists endpoints.
