# Mock DB Profile (H2) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a `mock` Spring profile that boots the app with an in-memory H2 database so Swagger UI can run locally without connecting to the real Postgres DB.

**Architecture:** Introduce an H2 runtime dependency and a `application-mock.properties` file that defines the H2 datasource and JPA settings. Default `application.properties` remains unchanged, so Postgres stays the default when no profile is set.

**Tech Stack:** Spring Boot 3.4.1, H2 (in-memory), Spring Data JPA

---

## File Structure

| File | Action | Responsibility |
|------|--------|----------------|
| `pom.xml` | Modify | Add H2 runtime dependency for mock profile |
| `src/main/resources/application-mock.properties` | Create | H2 datasource + JPA settings for mock profile |

---

### Task 1: Add H2 Runtime Dependency

**Files:**
- Modify: `pom.xml:88-101`

- [ ] **Step 1: Add H2 dependency after postgres**

Edit `pom.xml` and add the following dependency after the postgresql dependency:

```xml
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
```

- [ ] **Step 2: Commit**

```bash
git add pom.xml
git commit -m "build: add H2 runtime dependency for mock profile"
```

---

### Task 2: Add Mock Profile Properties

**Files:**
- Create: `src/main/resources/application-mock.properties`

- [ ] **Step 1: Create application-mock.properties**

Create `src/main/resources/application-mock.properties` with:

```
spring.datasource.url=jdbc:h2:mem:mockdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
```

- [ ] **Step 2: Commit**

```bash
git add src/main/resources/application-mock.properties
git commit -m "config: add mock profile datasource for H2"
```

---

### Task 3: Verify Mock Profile Boot

**Files:**
- None

- [ ] **Step 1: Run application with mock profile**

Run:

```bash
SPRING_PROFILES_ACTIVE=mock ./mvnw spring-boot:run
```

Expected: Application starts without Postgres connection errors.

- [ ] **Step 2: Manual Swagger UI check**

Open `http://localhost:8080/swagger-ui.html` and confirm the UI loads.

- [ ] **Step 3: Stop the application**

Stop the server with `Ctrl+C`.

---

## Verification Checklist

- [ ] H2 dependency added with runtime scope.
- [ ] `application-mock.properties` exists and uses H2 in-memory config.
- [ ] App boots with `SPRING_PROFILES_ACTIVE=mock` without contacting Postgres.
- [ ] Swagger UI loads locally.
