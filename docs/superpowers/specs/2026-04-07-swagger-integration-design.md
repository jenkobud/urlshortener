# Swagger/OpenAPI Integration Design

**Date:** 2026-04-07  
**Status:** Approved

## Overview

Add interactive API documentation using SpringDoc OpenAPI with Swagger UI. All endpoints will include descriptions, examples matching the roadmap specification, and proper error response documentation.

## Scope

**In scope:**
- SpringDoc OpenAPI dependency integration
- OpenAPI configuration class with API metadata
- Controller annotations (@Tag, @Operation, @ApiResponses)
- DTO annotations (@Schema) with example values
- Swagger UI accessible at `/swagger-ui.html`

**Out of scope:**
- Authentication/authorization in Swagger
- API versioning
- Custom Swagger UI themes

## Technology

- **Library:** `springdoc-openapi-starter-webmvc-ui` version 2.3.0
- **Spring Boot:** 3.4.1 (compatible)
- **Access Points:**
  - Swagger UI: `http://localhost:8080/swagger-ui.html`
  - OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Implementation Details

### 1. Maven Dependency

Add to `pom.xml`:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

### 2. OpenAPI Configuration

Create `src/main/java/com/roadmapsh/urlshortener/config/OpenApiConfig.java`:

```java
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI urlShortenerOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("URL Shortener API")
                .description("RESTful API for shortening URLs with CRUD operations and access statistics")
                .version("1.0")
                .contact(new Contact()
                    .name("URL Shortener")
                    .url("https://roadmap.sh/projects/url-shortening-service")));
    }
}
```

### 3. Controller Annotations

Add to `UrlShortenerController`:

**Class level:**
```java
@Tag(name = "URL Shortener", description = "Operations for shortening and managing URLs")
```

**Endpoint annotations:**

| Endpoint | @Operation summary | Response codes |
|----------|-------------------|----------------|
| POST /shorten | Create a new short URL | 201 Created, 400 Bad Request |
| GET /shorten/{shortCode} | Retrieve original URL by short code | 200 OK, 404 Not Found |
| PUT /shorten/{shortCode} | Update destination URL | 200 OK, 400 Bad Request, 404 Not Found |
| DELETE /shorten/{shortCode} | Delete a short URL | 204 No Content, 404 Not Found |
| GET /shorten/{shortCode}/stats | Get URL access statistics | 200 OK, 404 Not Found |

### 4. DTO Annotations with Examples

**UrlShortenerRequest:**
- `url`: example = "https://www.example.com/some/long/url"

**UrlShortenerResponse:**
- `id`: example = "abc123"
- `url`: example = "https://www.example.com/some/long/url"
- `shortCode`: example = "abc123"
- `createdAt`: example = "2021-09-01T12:00:00"
- `updatedAt`: example = "2021-09-01T12:00:00"

**UrlShortenerStatsResponse:**
- Inherits parent fields
- `accessCount`: example = 10

**ErrorResponse:**
- `timestamp`: example = "2021-09-01T12:00:00"
- `status`: example = 400
- `error`: example = "URL_INVALID"
- `message`: example = "URL cannot be empty"
- `path`: example = "/shorten"

## Files to Create/Modify

| File | Action |
|------|--------|
| `pom.xml` | Add springdoc-openapi dependency |
| `config/OpenApiConfig.java` | Create - API metadata |
| `controllers/UrlShortenerController.java` | Modify - Add OpenAPI annotations |
| `dtos/requests/UrlShortenerRequest.java` | Modify - Add @Schema |
| `dtos/responses/UrlShortenerResponse.java` | Modify - Add @Schema |
| `dtos/responses/UrlShortenerStatsResponse.java` | Modify - Add @Schema |
| `dtos/responses/ErrorResponse.java` | Modify - Add @Schema |

## Verification

After implementation:
1. Start application: `./mvnw spring-boot:run`
2. Open browser: `http://localhost:8080/swagger-ui.html`
3. Verify all 5 endpoints are documented
4. Test "Try it out" functionality
5. Verify examples match roadmap specification
