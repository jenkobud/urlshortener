# URL Shortener - Core API Completion Design

**Date:** 2026-04-07  
**Status:** Approved

## Overview

Complete the partially implemented URL shortener REST API by finishing the service layer with proper short code generation, URL validation, transaction management, and optimistic locking.

## Scope

**In scope:**
- Complete all 5 service methods (create, get, update, delete, stats)
- Random alphanumeric short code generation (6 characters)
- URL validation utility
- Transaction management with `@Transactional`
- Optimistic locking with `@Version`
- Access count tracking on retrieval

**Out of scope:**
- Frontend UI
- Redirect endpoint
- Authentication/authorization

## Architecture

```
Controller → Service → DAO → PostgreSQL
     ↓          ↓
    DTOs     Mapper ← Model
```

### New Components

| File | Purpose |
|------|---------|
| `utils/ShortCodeGenerator.java` | Generate random 6-char alphanumeric codes |
| `utils/UrlValidator.java` | Validate URL format (http/https, parseable) |

### Modified Components

| File | Changes |
|------|---------|
| `models/UrlShortener.java` | Add `@Version` field for optimistic locking |
| `services/impl/UrlShortenerServiceImpl.java` | Complete all 5 methods with `@Transactional` |
| `mappers/UrlShortenerMapper.java` | Add `fromModelToStatsDto()` method |
| `controllers/UrlShortenerController.java` | Add handler for `OptimisticLockException` |

## Short Code Generation

- **Charset:** A-Z, a-z, 0-9 (62 characters)
- **Length:** 6 characters (~56.8 billion combinations)
- **Algorithm:** SecureRandom + collision check with max 3 retries
- **Location:** `com.roadmapsh.urlshortener.utils.ShortCodeGenerator`

```java
// Pseudocode
public String generate() {
    for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
        String code = generateRandomCode(6);
        if (!dao.existsById(code)) {
            return code;
        }
    }
    throw new RuntimeException("Failed to generate unique short code");
}
```

## URL Validation

**Location:** `com.roadmapsh.urlshortener.utils.UrlValidator`

| Error Code | Condition |
|------------|-----------|
| `URL_EMPTY` | URL is null or blank |
| `URL_INVALID_FORMAT` | Cannot be parsed as URI |
| `URL_INVALID_SCHEME` | Not http or https |

```java
// Pseudocode
public static void validate(String url) throws InvalidUrlException {
    if (url == null || url.isBlank()) {
        throw new InvalidUrlException("URL_EMPTY", "URL cannot be empty");
    }
    try {
        URI uri = new URI(url);
        String scheme = uri.getScheme();
        if (scheme == null || (!scheme.equals("http") && !scheme.equals("https"))) {
            throw new InvalidUrlException("URL_INVALID_SCHEME", "URL must use http or https");
        }
    } catch (URISyntaxException e) {
        throw new InvalidUrlException("URL_INVALID_FORMAT", "Invalid URL format");
    }
}
```

## Service Methods

### createShortUrl
```
1. Validate URL using UrlValidator
2. Generate unique shortCode using ShortCodeGenerator
3. Create UrlShortener entity:
   - shortCode = generated code
   - url = request.url
   - creationDate = LocalDateTime.now()
   - updatedDate = LocalDateTime.now()
   - accessedTimes = 0
4. Save to DB via DAO.save()
5. Map to UrlShortenerResponse and return
```

### getOriginalUrl
```
1. Find by shortCode via DAO.findByShortCode()
2. If not found, return Optional.empty()
3. Increment accessedTimes
4. Save updated entity
5. Map to UrlShortenerResponse and return
```

### updateShortUrl
```
1. Validate new URL using UrlValidator
2. Find by shortCode via DAO.findByShortCode()
3. If not found, throw UrlNotFoundException
4. Update url field
5. Update updatedDate = LocalDateTime.now()
6. Save via DAO.save()
7. Map to UrlShortenerResponse and return
```

### deleteShortUrl
```
1. Check if exists via DAO.existsById(shortCode)
2. If not found, throw UrlNotFoundException
3. Delete via DAO.deleteById(shortCode)
```

### getUrlStatistics
```
1. Find by shortCode via DAO.findByShortCode()
2. If not found, return Optional.empty()
3. Map to UrlShortenerStatsResponse (includes accessCount)
4. Return
```

## Transaction Configuration

| Method | @Transactional | readOnly |
|--------|---------------|----------|
| createShortUrl | Yes | No |
| getOriginalUrl | Yes | No (increments counter) |
| updateShortUrl | Yes | No |
| deleteShortUrl | Yes | No |
| getUrlStatistics | Yes | Yes |

## Optimistic Locking

Add `@Version` field to `UrlShortener` model:

```java
@Version
private Long version;
```

This prevents lost updates when concurrent requests modify the same record. Hibernate automatically checks the version on update and throws `OptimisticLockException` on mismatch.

## Error Handling

| Exception | HTTP Status | When |
|-----------|-------------|------|
| `InvalidUrlException` | 400 Bad Request | URL validation fails |
| `UrlNotFoundException` | 404 Not Found | ShortCode doesn't exist |
| `OptimisticLockException` | 409 Conflict | Concurrent update detected |

### Controller Updates

Add exception handler for `OptimisticLockException`:

```java
@ExceptionHandler(OptimisticLockException.class)
public ResponseEntity<ErrorResponse> handleOptimisticLock(OptimisticLockException e) {
    ErrorResponse err = new ErrorResponse(
        LocalDateTime.now(), 409, "CONFLICT",
        "Resource was modified by another request", "/shorten"
    );
    return new ResponseEntity<>(err, HttpStatus.CONFLICT);
}
```

## Mapper Updates

Add method to `UrlShortenerMapper`:

```java
public static UrlShortenerStatsResponse fromModelToStatsDto(UrlShortener model) {
    UrlShortenerStatsResponse dto = new UrlShortenerStatsResponse();
    dto.setId(model.getShortCode());
    dto.setShortCode(model.getShortCode());
    dto.setUrl(model.getUrl());
    dto.setCreatedAt(model.getCreationDate());
    dto.setUpdatedAt(model.getUpdatedDate());
    dto.setAccessCount(model.getAccessedTimes());
    return dto;
}
```

Also update `fromModelToDto` to set the `id` field:

```java
public static UrlShortenerResponse fromModelToDto(UrlShortener model) {
    UrlShortenerResponse dto = new UrlShortenerResponse();
    dto.setId(model.getShortCode());  // Add this line
    dto.setShortCode(model.getShortCode());
    dto.setUrl(model.getUrl());
    dto.setCreatedAt(model.getCreationDate());
    dto.setUpdatedAt(model.getUpdatedDate());
    return dto;
}
```

## Database Considerations

- Table `urlshortener` already exists in PostgreSQL
- `ddl-auto=validate` ensures schema matches entity
- If adding `@Version`, ensure `version` column exists in DB (BIGINT, nullable, default NULL)

## Files to Create/Modify

### New Files
1. `src/main/java/com/roadmapsh/urlshortener/utils/ShortCodeGenerator.java`
2. `src/main/java/com/roadmapsh/urlshortener/utils/UrlValidator.java`

### Modified Files
1. `src/main/java/com/roadmapsh/urlshortener/models/UrlShortener.java` - Add @Version
2. `src/main/java/com/roadmapsh/urlshortener/services/impl/UrlShortenerServiceImpl.java` - Complete all methods
3. `src/main/java/com/roadmapsh/urlshortener/mappers/UrlShortenerMapper.java` - Add stats mapping
4. `src/main/java/com/roadmapsh/urlshortener/controllers/UrlShortenerController.java` - Add conflict handler
