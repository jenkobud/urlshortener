# Swagger/OpenAPI Integration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add interactive API documentation using SpringDoc OpenAPI with Swagger UI

**Architecture:** Add springdoc-openapi dependency, create OpenAPI config class with metadata, annotate controller with @Tag/@Operation/@ApiResponses, annotate DTOs with @Schema and examples matching roadmap spec

**Tech Stack:** SpringDoc OpenAPI 2.3.0, Spring Boot 3.4.1, Swagger UI

---

## File Structure

| File | Action | Responsibility |
|------|--------|----------------|
| `pom.xml` | Modify | Add springdoc-openapi-starter-webmvc-ui dependency |
| `src/main/java/com/roadmapsh/urlshortener/config/OpenApiConfig.java` | Create | API metadata (title, description, version, contact) |
| `src/main/java/com/roadmapsh/urlshortener/controllers/UrlShortenerController.java` | Modify | Add @Tag, @Operation, @ApiResponses annotations |
| `src/main/java/com/roadmapsh/urlshortener/dtos/requests/UrlShortenerRequest.java` | Modify | Add @Schema with example |
| `src/main/java/com/roadmapsh/urlshortener/dtos/responses/UrlShortenerResponse.java` | Modify | Add @Schema with examples |
| `src/main/java/com/roadmapsh/urlshortener/dtos/responses/UrlShortenerStatsResponse.java` | Modify | Add @Schema with accessCount example |
| `src/main/java/com/roadmapsh/urlshortener/dtos/responses/ErrorResponse.java` | Modify | Add @Schema with examples |

---

### Task 1: Add SpringDoc OpenAPI Dependency

**Files:**
- Modify: `pom.xml:93-95` (add after postgresql dependency)

- [ ] **Step 1: Add springdoc-openapi dependency to pom.xml**

Edit `pom.xml` and add the following dependency after the postgresql dependency (after line 94):

```xml
		<dependency>
			<groupId>org.springdoc</groupId>
			<artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
			<version>2.3.0</version>
		</dependency>
```

- [ ] **Step 2: Commit**

```bash
git add pom.xml
git commit -m "build: add springdoc-openapi dependency for Swagger UI"
```

---

### Task 2: Create OpenAPI Configuration

**Files:**
- Create: `src/main/java/com/roadmapsh/urlshortener/config/OpenApiConfig.java`

- [ ] **Step 1: Create OpenApiConfig.java**

Create the file `src/main/java/com/roadmapsh/urlshortener/config/OpenApiConfig.java` with:

```java
package com.roadmapsh.urlshortener.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

- [ ] **Step 2: Commit**

```bash
git add src/main/java/com/roadmapsh/urlshortener/config/OpenApiConfig.java
git commit -m "feat: add OpenAPI configuration with API metadata"
```

---

### Task 3: Annotate Request DTO

**Files:**
- Modify: `src/main/java/com/roadmapsh/urlshortener/dtos/requests/UrlShortenerRequest.java`

- [ ] **Step 1: Add @Schema annotation to UrlShortenerRequest**

Edit `src/main/java/com/roadmapsh/urlshortener/dtos/requests/UrlShortenerRequest.java`:

Add import after existing imports:
```java
import io.swagger.v3.oas.annotations.media.Schema;
```

Replace the url field declaration:
```java
    @NotNull
    @NotEmpty
    @Schema(description = "The URL to shorten", example = "https://www.example.com/some/long/url")
    private String url;
```

The complete file should be:

```java
package com.roadmapsh.urlshortener.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Schema(description = "Request body for creating or updating a short URL")
public class UrlShortenerRequest {
    @NotNull
    @NotEmpty
    @Schema(description = "The URL to shorten", example = "https://www.example.com/some/long/url")
    private String url;
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/com/roadmapsh/urlshortener/dtos/requests/UrlShortenerRequest.java
git commit -m "docs: add OpenAPI schema annotations to UrlShortenerRequest"
```

---

### Task 4: Annotate Response DTO

**Files:**
- Modify: `src/main/java/com/roadmapsh/urlshortener/dtos/responses/UrlShortenerResponse.java`

- [ ] **Step 1: Add @Schema annotations to UrlShortenerResponse**

Edit `src/main/java/com/roadmapsh/urlshortener/dtos/responses/UrlShortenerResponse.java`:

Replace the entire file with:

```java
package com.roadmapsh.urlshortener.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Schema(description = "Response containing short URL details")
public class UrlShortenerResponse {
    @Schema(description = "Unique identifier (same as shortCode)", example = "abc123")
    protected String id;

    @Schema(description = "The original URL", example = "https://www.example.com/some/long/url")
    protected String url;

    @Schema(description = "The generated short code", example = "abc123")
    protected String shortCode;

    @Schema(description = "Timestamp when the short URL was created", example = "2021-09-01T12:00:00")
    protected LocalDateTime createdAt;

    @Schema(description = "Timestamp when the short URL was last updated", example = "2021-09-01T12:00:00")
    protected LocalDateTime updatedAt;
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/com/roadmapsh/urlshortener/dtos/responses/UrlShortenerResponse.java
git commit -m "docs: add OpenAPI schema annotations to UrlShortenerResponse"
```

---

### Task 5: Annotate Stats Response DTO

**Files:**
- Modify: `src/main/java/com/roadmapsh/urlshortener/dtos/responses/UrlShortenerStatsResponse.java`

- [ ] **Step 1: Add @Schema annotations to UrlShortenerStatsResponse**

Edit `src/main/java/com/roadmapsh/urlshortener/dtos/responses/UrlShortenerStatsResponse.java`:

Replace the entire file with:

```java
package com.roadmapsh.urlshortener.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Schema(description = "Response containing short URL details with access statistics")
public class UrlShortenerStatsResponse extends UrlShortenerResponse {
    @Schema(description = "Number of times this short URL has been accessed", example = "10")
    private Number accessCount = 0;
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/com/roadmapsh/urlshortener/dtos/responses/UrlShortenerStatsResponse.java
git commit -m "docs: add OpenAPI schema annotations to UrlShortenerStatsResponse"
```

---

### Task 6: Annotate Error Response DTO

**Files:**
- Modify: `src/main/java/com/roadmapsh/urlshortener/dtos/responses/ErrorResponse.java`

- [ ] **Step 1: Add @Schema annotations to ErrorResponse**

Edit `src/main/java/com/roadmapsh/urlshortener/dtos/responses/ErrorResponse.java`:

Replace the entire file with:

```java
package com.roadmapsh.urlshortener.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Schema(description = "Error response returned when a request fails")
public class ErrorResponse {
    @Schema(description = "Timestamp when the error occurred", example = "2021-09-01T12:00:00")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Error code", example = "URL_INVALID")
    private String error;

    @Schema(description = "Human-readable error message", example = "URL cannot be empty")
    private String message;

    @Schema(description = "Request path that caused the error", example = "/shorten")
    private String path;
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/com/roadmapsh/urlshortener/dtos/responses/ErrorResponse.java
git commit -m "docs: add OpenAPI schema annotations to ErrorResponse"
```

---

### Task 7: Annotate Controller with OpenAPI

**Files:**
- Modify: `src/main/java/com/roadmapsh/urlshortener/controllers/UrlShortenerController.java`

- [ ] **Step 1: Add imports for OpenAPI annotations**

Edit `src/main/java/com/roadmapsh/urlshortener/controllers/UrlShortenerController.java`:

Add imports after the existing imports (after line 18):

```java
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
```

- [ ] **Step 2: Add @Tag annotation to controller class**

Add the @Tag annotation before @RestController:

```java
@Tag(name = "URL Shortener", description = "Operations for shortening and managing URLs")
@RestController
@RequestMapping("/shorten")
public class UrlShortenerController {
```

- [ ] **Step 3: Annotate createShortUrl endpoint**

Replace the createShortUrl method (lines 32-42) with:

```java
    @Operation(summary = "Create a new short URL", description = "Creates a shortened URL for the provided long URL")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Short URL created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UrlShortenerResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid URL provided",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<Object> createShortUrl(@RequestBody @Valid UrlShortenerRequest request) {
        log.info("Starting shortening of {}", request.getUrl());
        try {
            UrlShortenerResponse response = urlShortenerService.createShortUrl(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (InvalidUrlException e) {
            ErrorResponse err = new ErrorResponse(LocalDateTime.now(), 400, e.getErrorCode() , e.getMessage(), "/shorten");
            return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
        }
    }
```

- [ ] **Step 4: Annotate getOriginalUrl endpoint**

Replace the getOriginalUrl method (lines 44-49) with:

```java
    @Operation(summary = "Retrieve original URL by short code", description = "Returns the original URL and increments the access counter")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "URL found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UrlShortenerResponse.class))),
        @ApiResponse(responseCode = "404", description = "Short URL not found")
    })
    @GetMapping("/{shortCode}")
    public ResponseEntity<UrlShortenerResponse> getOriginalUrl(
            @Parameter(description = "The short code to look up", example = "abc123") @PathVariable String shortCode) {
        Optional<UrlShortenerResponse> response = urlShortenerService.getOriginalUrl(shortCode);
        return response.map(urlResponse -> new ResponseEntity<>(urlResponse, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
```

- [ ] **Step 5: Annotate updateShortUrl endpoint**

Replace the updateShortUrl method (lines 51-61) with:

```java
    @Operation(summary = "Update destination URL", description = "Updates the original URL for an existing short code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "URL updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UrlShortenerResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid URL provided",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Short URL not found")
    })
    @PutMapping("/{shortCode}")
    public ResponseEntity<UrlShortenerResponse> updateShortUrl(
            @Parameter(description = "The short code to update", example = "abc123") @PathVariable String shortCode,
            @RequestBody UrlShortenerRequest request) {
        try {
            UrlShortenerResponse response = urlShortenerService.updateShortUrl(shortCode, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (InvalidUrlException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (UrlNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
```

- [ ] **Step 6: Annotate deleteShortUrl endpoint**

Replace the deleteShortUrl method (lines 63-71) with:

```java
    @Operation(summary = "Delete a short URL", description = "Permanently removes a short URL")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "URL deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Short URL not found")
    })
    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteShortUrl(
            @Parameter(description = "The short code to delete", example = "abc123") @PathVariable String shortCode) {
        try {
            urlShortenerService.deleteShortUrl(shortCode);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (UrlNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
```

- [ ] **Step 7: Annotate getUrlStatistics endpoint**

Replace the getUrlStatistics method (lines 73-78) with:

```java
    @Operation(summary = "Get URL access statistics", description = "Returns the short URL details including access count")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UrlShortenerStatsResponse.class))),
        @ApiResponse(responseCode = "404", description = "Short URL not found")
    })
    @GetMapping("/{shortCode}/stats")
    public ResponseEntity<UrlShortenerStatsResponse> getUrlStatistics(
            @Parameter(description = "The short code to get statistics for", example = "abc123") @PathVariable String shortCode) {
        Optional<UrlShortenerStatsResponse> response = urlShortenerService.getUrlStatistics(shortCode);
        return response.map(statsResponse -> new ResponseEntity<>(statsResponse, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
```

- [ ] **Step 8: Annotate OptimisticLockException handler**

Replace the handleOptimisticLockException method (lines 80-90) with:

```java
    @Operation(hidden = true)
    @ExceptionHandler(jakarta.persistence.OptimisticLockException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockException(jakarta.persistence.OptimisticLockException e) {
        ErrorResponse err = new ErrorResponse(
            LocalDateTime.now(), 
            409, 
            "CONFLICT", 
            "Resource was modified by another request. Please retry.", 
            "/shorten"
        );
        return new ResponseEntity<>(err, HttpStatus.CONFLICT);
    }
```

- [ ] **Step 9: Commit**

```bash
git add src/main/java/com/roadmapsh/urlshortener/controllers/UrlShortenerController.java
git commit -m "docs: add OpenAPI annotations to UrlShortenerController"
```

---

### Task 8: Verify Swagger UI (Manual)

**Note:** This task requires a running application. If Java is not available in the environment, this verification should be done manually by the developer.

- [ ] **Step 1: Start the application**

```bash
./mvnw spring-boot:run
```

- [ ] **Step 2: Open Swagger UI**

Open browser and navigate to: `http://localhost:8080/swagger-ui.html`

- [ ] **Step 3: Verify documentation**

Check that:
- API title shows "URL Shortener API"
- All 5 endpoints are listed under "URL Shortener" tag
- Examples match the roadmap specification
- "Try it out" functionality works for each endpoint

---

## Verification Checklist

After all tasks are complete:

- [ ] SpringDoc dependency added to pom.xml
- [ ] OpenApiConfig.java creates API metadata bean
- [ ] All 4 DTOs have @Schema annotations with examples
- [ ] Controller has @Tag annotation
- [ ] All 5 endpoints have @Operation and @ApiResponses
- [ ] Path parameters have @Parameter annotations
- [ ] Examples match roadmap specification values
