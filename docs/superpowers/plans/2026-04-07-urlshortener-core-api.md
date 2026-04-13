# URL Shortener Core API Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Complete the URL shortener REST API service layer with proper short code generation, URL validation, transaction management, and optimistic locking.

**Architecture:** Layered architecture (Controller → Service → DAO → PostgreSQL). Adding two utility classes (ShortCodeGenerator, UrlValidator) and completing the service implementation with @Transactional and @Version for concurrency control.

**Tech Stack:** Java 21, Spring Boot 3.4.1, Spring Data JPA, PostgreSQL, Lombok, JUnit 5

---

## File Structure

| File | Responsibility |
|------|----------------|
| `src/main/java/.../utils/UrlValidator.java` | Static utility to validate URL format |
| `src/main/java/.../utils/ShortCodeGenerator.java` | Generate random 6-char alphanumeric codes |
| `src/main/java/.../models/UrlShortener.java` | JPA entity (add @Version field) |
| `src/main/java/.../mappers/UrlShortenerMapper.java` | Model ↔ DTO mapping (add stats method) |
| `src/main/java/.../services/impl/UrlShortenerServiceImpl.java` | Complete all 5 service methods |
| `src/main/java/.../controllers/UrlShortenerController.java` | Add OptimisticLockException handler |
| `src/main/java/.../errors/InvalidUrlException.java` | Add errorCode field if missing |
| `src/test/java/.../utils/UrlValidatorTest.java` | Unit tests for URL validation |
| `src/test/java/.../utils/ShortCodeGeneratorTest.java` | Unit tests for code generation |
| `src/test/java/.../services/UrlShortenerServiceImplTest.java` | Unit tests for service layer |

---

## Task 1: URL Validator Utility

**Files:**
- Create: `src/main/java/com/roadmapsh/urlshortener/utils/UrlValidator.java`
- Create: `src/test/java/com/roadmapsh/urlshortener/utils/UrlValidatorTest.java`

- [ ] **Step 1: Create test file with first failing test (empty URL)**

```java
package com.roadmapsh.urlshortener.utils;

import com.roadmapsh.urlshortener.errors.InvalidUrlException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UrlValidatorTest {

    @Test
    void validate_shouldThrowException_whenUrlIsNull() {
        InvalidUrlException ex = assertThrows(InvalidUrlException.class, 
            () -> UrlValidator.validate(null));
        assertEquals("URL_EMPTY", ex.getErrorCode());
    }

    @Test
    void validate_shouldThrowException_whenUrlIsBlank() {
        InvalidUrlException ex = assertThrows(InvalidUrlException.class, 
            () -> UrlValidator.validate("   "));
        assertEquals("URL_EMPTY", ex.getErrorCode());
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw test -Dtest=UrlValidatorTest -q`
Expected: Compilation error - UrlValidator class does not exist

- [ ] **Step 3: Create UrlValidator with minimal implementation for empty check**

```java
package com.roadmapsh.urlshortener.utils;

import com.roadmapsh.urlshortener.errors.InvalidUrlException;

public final class UrlValidator {

    private UrlValidator() {
        // Private constructor to prevent instantiation
    }

    public static void validate(String url) throws InvalidUrlException {
        if (url == null || url.isBlank()) {
            throw new InvalidUrlException("URL_EMPTY", "URL cannot be empty");
        }
    }
}
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `./mvnw test -Dtest=UrlValidatorTest -q`
Expected: 2 tests PASS

- [ ] **Step 5: Add failing test for invalid URL format**

Add to `UrlValidatorTest.java`:

```java
    @Test
    void validate_shouldThrowException_whenUrlIsInvalidFormat() {
        InvalidUrlException ex = assertThrows(InvalidUrlException.class, 
            () -> UrlValidator.validate("not a valid url"));
        assertEquals("URL_INVALID_FORMAT", ex.getErrorCode());
    }
```

- [ ] **Step 6: Run test to verify it fails**

Run: `./mvnw test -Dtest=UrlValidatorTest#validate_shouldThrowException_whenUrlIsInvalidFormat -q`
Expected: FAIL - no exception thrown or wrong error code

- [ ] **Step 7: Implement URL format validation**

Update `UrlValidator.java`:

```java
package com.roadmapsh.urlshortener.utils;

import com.roadmapsh.urlshortener.errors.InvalidUrlException;

import java.net.URI;
import java.net.URISyntaxException;

public final class UrlValidator {

    private UrlValidator() {
        // Private constructor to prevent instantiation
    }

    public static void validate(String url) throws InvalidUrlException {
        if (url == null || url.isBlank()) {
            throw new InvalidUrlException("URL_EMPTY", "URL cannot be empty");
        }
        
        try {
            URI uri = new URI(url);
            if (uri.getScheme() == null) {
                throw new InvalidUrlException("URL_INVALID_FORMAT", "Invalid URL format");
            }
        } catch (URISyntaxException e) {
            throw new InvalidUrlException("URL_INVALID_FORMAT", "Invalid URL format");
        }
    }
}
```

- [ ] **Step 8: Run tests to verify they pass**

Run: `./mvnw test -Dtest=UrlValidatorTest -q`
Expected: 3 tests PASS

- [ ] **Step 9: Add failing test for invalid scheme**

Add to `UrlValidatorTest.java`:

```java
    @Test
    void validate_shouldThrowException_whenSchemeIsNotHttpOrHttps() {
        InvalidUrlException ex = assertThrows(InvalidUrlException.class, 
            () -> UrlValidator.validate("ftp://example.com/file.txt"));
        assertEquals("URL_INVALID_SCHEME", ex.getErrorCode());
    }

    @Test
    void validate_shouldPass_whenUrlIsValidHttp() {
        assertDoesNotThrow(() -> UrlValidator.validate("http://example.com"));
    }

    @Test
    void validate_shouldPass_whenUrlIsValidHttps() {
        assertDoesNotThrow(() -> UrlValidator.validate("https://example.com/path?query=1"));
    }
```

- [ ] **Step 10: Run test to verify scheme test fails**

Run: `./mvnw test -Dtest=UrlValidatorTest#validate_shouldThrowException_whenSchemeIsNotHttpOrHttps -q`
Expected: FAIL - no exception thrown

- [ ] **Step 11: Implement scheme validation**

Update `UrlValidator.java`:

```java
package com.roadmapsh.urlshortener.utils;

import com.roadmapsh.urlshortener.errors.InvalidUrlException;

import java.net.URI;
import java.net.URISyntaxException;

public final class UrlValidator {

    private UrlValidator() {
        // Private constructor to prevent instantiation
    }

    public static void validate(String url) throws InvalidUrlException {
        if (url == null || url.isBlank()) {
            throw new InvalidUrlException("URL_EMPTY", "URL cannot be empty");
        }
        
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if (scheme == null) {
                throw new InvalidUrlException("URL_INVALID_FORMAT", "Invalid URL format");
            }
            if (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")) {
                throw new InvalidUrlException("URL_INVALID_SCHEME", "URL must use http or https scheme");
            }
        } catch (URISyntaxException e) {
            throw new InvalidUrlException("URL_INVALID_FORMAT", "Invalid URL format");
        }
    }
}
```

- [ ] **Step 12: Run all tests to verify they pass**

Run: `./mvnw test -Dtest=UrlValidatorTest -q`
Expected: 6 tests PASS

- [ ] **Step 13: Commit**

```bash
git add src/main/java/com/roadmapsh/urlshortener/utils/UrlValidator.java src/test/java/com/roadmapsh/urlshortener/utils/UrlValidatorTest.java
git commit -m "feat: add UrlValidator utility with URL format and scheme validation"
```

---

## Task 2: Update InvalidUrlException to support error codes

**Files:**
- Modify: `src/main/java/com/roadmapsh/urlshortener/errors/InvalidUrlException.java`

- [ ] **Step 1: Check current InvalidUrlException implementation**

Read the current file to understand what needs to be added.

- [ ] **Step 2: Update InvalidUrlException with errorCode field**

```java
package com.roadmapsh.urlshortener.errors;

import lombok.Getter;

@Getter
public class InvalidUrlException extends Exception {
    
    private final String errorCode;
    
    public InvalidUrlException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public InvalidUrlException(String message) {
        super(message);
        this.errorCode = "URL_INVALID";
    }
}
```

- [ ] **Step 3: Run UrlValidator tests to verify exception works**

Run: `./mvnw test -Dtest=UrlValidatorTest -q`
Expected: 6 tests PASS

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/roadmapsh/urlshortener/errors/InvalidUrlException.java
git commit -m "feat: add errorCode field to InvalidUrlException"
```

---

## Task 3: Short Code Generator Utility

**Files:**
- Create: `src/main/java/com/roadmapsh/urlshortener/utils/ShortCodeGenerator.java`
- Create: `src/test/java/com/roadmapsh/urlshortener/utils/ShortCodeGeneratorTest.java`

- [ ] **Step 1: Create test file with first failing test**

```java
package com.roadmapsh.urlshortener.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

class ShortCodeGeneratorTest {

    @Test
    void generate_shouldReturnCodeOfLength6() {
        ShortCodeGenerator generator = new ShortCodeGenerator();
        String code = generator.generate();
        assertEquals(6, code.length());
    }

    @Test
    void generate_shouldReturnOnlyAlphanumericCharacters() {
        ShortCodeGenerator generator = new ShortCodeGenerator();
        String code = generator.generate();
        assertTrue(code.matches("^[A-Za-z0-9]+$"), "Code should only contain alphanumeric characters");
    }

    @RepeatedTest(10)
    void generate_shouldReturnDifferentCodesOnMultipleCalls() {
        ShortCodeGenerator generator = new ShortCodeGenerator();
        String code1 = generator.generate();
        String code2 = generator.generate();
        // Note: There's a tiny chance of collision, but statistically improbable
        assertNotEquals(code1, code2, "Generated codes should be different");
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw test -Dtest=ShortCodeGeneratorTest -q`
Expected: Compilation error - ShortCodeGenerator class does not exist

- [ ] **Step 3: Create ShortCodeGenerator with minimal implementation**

```java
package com.roadmapsh.urlshortener.utils;

import java.security.SecureRandom;

public class ShortCodeGenerator {
    
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();
    
    public String generate() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }
        return code.toString();
    }
}
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `./mvnw test -Dtest=ShortCodeGeneratorTest -q`
Expected: All tests PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/roadmapsh/urlshortener/utils/ShortCodeGenerator.java src/test/java/com/roadmapsh/urlshortener/utils/ShortCodeGeneratorTest.java
git commit -m "feat: add ShortCodeGenerator utility for random alphanumeric codes"
```

---

## Task 4: Add @Version to UrlShortener Model

**Files:**
- Modify: `src/main/java/com/roadmapsh/urlshortener/models/UrlShortener.java`

- [ ] **Step 1: Add @Version field to model**

Update `UrlShortener.java` to add after `accessedTimes` field:

```java
@Version
private Long version;
```

Full updated file:

```java
package com.roadmapsh.urlshortener.models;

import com.roadmapsh.urlshortener.HibernateUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "urlshortener")
@Getter
@Setter
@ToString
public class UrlShortener {
    @Id
    private String shortCode;
    private String url;
    private LocalDateTime creationDate;
    private LocalDateTime updatedDate;
    private int accessedTimes;
    
    @Version
    private Long version;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (HibernateUtils.getHibernateEffectiveClass(this) != HibernateUtils.getHibernateEffectiveClass(o)) return false;
        UrlShortener that = (UrlShortener) o;
        return getShortCode() != null && Objects.equals(getShortCode(), that.getShortCode());
    }

    @Override
    public final int hashCode() {
        return HibernateUtils.getHibernateEffectiveClass(this).hashCode();
    }

}
```

- [ ] **Step 2: Verify application compiles**

Run: `./mvnw compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/roadmapsh/urlshortener/models/UrlShortener.java
git commit -m "feat: add @Version field for optimistic locking"
```

**Note:** The database table needs a `version` column (BIGINT, nullable). If using ddl-auto=validate, add this column manually:
```sql
ALTER TABLE urlshortener ADD COLUMN version BIGINT;
```

---

## Task 5: Update UrlShortenerMapper

**Files:**
- Modify: `src/main/java/com/roadmapsh/urlshortener/mappers/UrlShortenerMapper.java`

- [ ] **Step 1: Update fromModelToDto to set id field**

Update the existing method:

```java
public static UrlShortenerResponse fromModelToDto(@NotNull UrlShortener urlShortener) {
    UrlShortenerResponse dto = new UrlShortenerResponse();
    dto.setId(urlShortener.getShortCode());
    dto.setShortCode(urlShortener.getShortCode());
    dto.setUrl(urlShortener.getUrl());
    dto.setCreatedAt(urlShortener.getCreationDate());
    dto.setUpdatedAt(urlShortener.getUpdatedDate());
    return dto;
}
```

- [ ] **Step 2: Add fromModelToStatsDto method**

Add new method:

```java
public static UrlShortenerStatsResponse fromModelToStatsDto(@NotNull UrlShortener urlShortener) {
    UrlShortenerStatsResponse dto = new UrlShortenerStatsResponse();
    dto.setId(urlShortener.getShortCode());
    dto.setShortCode(urlShortener.getShortCode());
    dto.setUrl(urlShortener.getUrl());
    dto.setCreatedAt(urlShortener.getCreationDate());
    dto.setUpdatedAt(urlShortener.getUpdatedDate());
    dto.setAccessCount(urlShortener.getAccessedTimes());
    return dto;
}
```

- [ ] **Step 3: Add import for UrlShortenerStatsResponse**

Full updated file:

```java
package com.roadmapsh.urlshortener.mappers;

import com.roadmapsh.urlshortener.dtos.responses.UrlShortenerResponse;
import com.roadmapsh.urlshortener.dtos.responses.UrlShortenerStatsResponse;
import com.roadmapsh.urlshortener.models.UrlShortener;
import jakarta.validation.constraints.NotNull;

public final class UrlShortenerMapper {

    private UrlShortenerMapper() {
        // Private constructor to prevent instantiation
    }

    public static UrlShortenerResponse fromModelToDto(@NotNull UrlShortener urlShortener) {
        UrlShortenerResponse dto = new UrlShortenerResponse();
        dto.setId(urlShortener.getShortCode());
        dto.setShortCode(urlShortener.getShortCode());
        dto.setUrl(urlShortener.getUrl());
        dto.setCreatedAt(urlShortener.getCreationDate());
        dto.setUpdatedAt(urlShortener.getUpdatedDate());
        return dto;
    }

    public static UrlShortenerStatsResponse fromModelToStatsDto(@NotNull UrlShortener urlShortener) {
        UrlShortenerStatsResponse dto = new UrlShortenerStatsResponse();
        dto.setId(urlShortener.getShortCode());
        dto.setShortCode(urlShortener.getShortCode());
        dto.setUrl(urlShortener.getUrl());
        dto.setCreatedAt(urlShortener.getCreationDate());
        dto.setUpdatedAt(urlShortener.getUpdatedDate());
        dto.setAccessCount(urlShortener.getAccessedTimes());
        return dto;
    }
}
```

- [ ] **Step 4: Verify application compiles**

Run: `./mvnw compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/roadmapsh/urlshortener/mappers/UrlShortenerMapper.java
git commit -m "feat: update mapper with id field and stats DTO mapping"
```

---

## Task 6: Implement createShortUrl Service Method

**Files:**
- Modify: `src/main/java/com/roadmapsh/urlshortener/services/impl/UrlShortenerServiceImpl.java`
- Create: `src/test/java/com/roadmapsh/urlshortener/services/UrlShortenerServiceImplTest.java`

- [ ] **Step 1: Create test file with failing test for createShortUrl**

```java
package com.roadmapsh.urlshortener.services;

import com.roadmapsh.urlshortener.daos.UrlShortenerDAO;
import com.roadmapsh.urlshortener.dtos.requests.UrlShortenerRequest;
import com.roadmapsh.urlshortener.dtos.responses.UrlShortenerResponse;
import com.roadmapsh.urlshortener.errors.InvalidUrlException;
import com.roadmapsh.urlshortener.models.UrlShortener;
import com.roadmapsh.urlshortener.services.impl.UrlShortenerServiceImpl;
import com.roadmapsh.urlshortener.utils.ShortCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceImplTest {

    @Mock
    private UrlShortenerDAO urlShortenerDAO;

    @Mock
    private ShortCodeGenerator shortCodeGenerator;

    private UrlShortenerServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UrlShortenerServiceImpl(urlShortenerDAO, shortCodeGenerator);
    }

    @Test
    void createShortUrl_shouldReturnResponse_whenUrlIsValid() throws InvalidUrlException {
        // Arrange
        UrlShortenerRequest request = new UrlShortenerRequest();
        request.setUrl("https://example.com/long/url");
        
        when(shortCodeGenerator.generate()).thenReturn("abc123");
        when(urlShortenerDAO.existsById("abc123")).thenReturn(false);
        when(urlShortenerDAO.save(any(UrlShortener.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        UrlShortenerResponse response = service.createShortUrl(request);

        // Assert
        assertNotNull(response);
        assertEquals("abc123", response.getShortCode());
        assertEquals("https://example.com/long/url", response.getUrl());
        assertNotNull(response.getCreatedAt());
        
        ArgumentCaptor<UrlShortener> captor = ArgumentCaptor.forClass(UrlShortener.class);
        verify(urlShortenerDAO).save(captor.capture());
        assertEquals(0, captor.getValue().getAccessedTimes());
    }

    @Test
    void createShortUrl_shouldThrowException_whenUrlIsInvalid() {
        // Arrange
        UrlShortenerRequest request = new UrlShortenerRequest();
        request.setUrl("not-a-valid-url");

        // Act & Assert
        assertThrows(InvalidUrlException.class, () -> service.createShortUrl(request));
        verify(urlShortenerDAO, never()).save(any());
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./mvnw test -Dtest=UrlShortenerServiceImplTest -q`
Expected: FAIL - constructor doesn't accept ShortCodeGenerator

- [ ] **Step 3: Update UrlShortenerServiceImpl with ShortCodeGenerator dependency and createShortUrl**

```java
package com.roadmapsh.urlshortener.services.impl;

import com.roadmapsh.urlshortener.daos.UrlShortenerDAO;
import com.roadmapsh.urlshortener.dtos.requests.UrlShortenerRequest;
import com.roadmapsh.urlshortener.dtos.responses.UrlShortenerResponse;
import com.roadmapsh.urlshortener.dtos.responses.UrlShortenerStatsResponse;
import com.roadmapsh.urlshortener.errors.InvalidUrlException;
import com.roadmapsh.urlshortener.errors.UrlNotFoundException;
import com.roadmapsh.urlshortener.mappers.UrlShortenerMapper;
import com.roadmapsh.urlshortener.models.UrlShortener;
import com.roadmapsh.urlshortener.services.UrlShortenerService;
import com.roadmapsh.urlshortener.utils.ShortCodeGenerator;
import com.roadmapsh.urlshortener.utils.UrlValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class UrlShortenerServiceImpl implements UrlShortenerService {

    private static final int MAX_RETRIES = 3;

    private final UrlShortenerDAO urlShortenerDAO;
    private final ShortCodeGenerator shortCodeGenerator;

    public UrlShortenerServiceImpl(UrlShortenerDAO urlShortenerDAO, ShortCodeGenerator shortCodeGenerator) {
        this.urlShortenerDAO = urlShortenerDAO;
        this.shortCodeGenerator = shortCodeGenerator;
    }

    @Override
    @Transactional
    public UrlShortenerResponse createShortUrl(UrlShortenerRequest request) throws InvalidUrlException {
        UrlValidator.validate(request.getUrl());
        
        String shortCode = generateUniqueShortCode();
        log.info("Shortening URL: {} to: {}", request.getUrl(), shortCode);
        
        UrlShortener entity = new UrlShortener();
        entity.setShortCode(shortCode);
        entity.setUrl(request.getUrl());
        entity.setCreationDate(LocalDateTime.now());
        entity.setUpdatedDate(LocalDateTime.now());
        entity.setAccessedTimes(0);
        
        UrlShortener saved = urlShortenerDAO.save(entity);
        return UrlShortenerMapper.fromModelToDto(saved);
    }
    
    private String generateUniqueShortCode() {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            String code = shortCodeGenerator.generate();
            if (!urlShortenerDAO.existsById(code)) {
                return code;
            }
            log.warn("Short code collision detected: {}, retrying...", code);
        }
        throw new RuntimeException("Failed to generate unique short code after " + MAX_RETRIES + " attempts");
    }

    @Override
    public Optional<UrlShortenerResponse> getOriginalUrl(String shortCode) {
        UrlShortener shortenerModel = urlShortenerDAO.getReferenceById(shortCode);
        try {
            UrlShortenerResponse response = UrlShortenerMapper.fromModelToDto(shortenerModel);
            return Optional.of(response);
        } catch (Exception e) {
            log.error("Error retrieving original URL for short code {}: {}", shortCode, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public UrlShortenerResponse updateShortUrl(String shortCode, UrlShortenerRequest request) throws InvalidUrlException, UrlNotFoundException {
        throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public void deleteShortUrl(String shortCode) throws UrlNotFoundException {
        throw new RuntimeException("NOT IMPLEMENTED");
    }

    @Override
    public Optional<UrlShortenerStatsResponse> getUrlStatistics(String shortCode) {
        throw new RuntimeException("NOT IMPLEMENTED");
    }
}
```

- [ ] **Step 4: Register ShortCodeGenerator as a Spring bean**

Create `src/main/java/com/roadmapsh/urlshortener/config/AppConfig.java`:

```java
package com.roadmapsh.urlshortener.config;

import com.roadmapsh.urlshortener.utils.ShortCodeGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ShortCodeGenerator shortCodeGenerator() {
        return new ShortCodeGenerator();
    }
}
```

- [ ] **Step 5: Run tests to verify they pass**

Run: `./mvnw test -Dtest=UrlShortenerServiceImplTest -q`
Expected: 2 tests PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/roadmapsh/urlshortener/services/impl/UrlShortenerServiceImpl.java \
        src/main/java/com/roadmapsh/urlshortener/config/AppConfig.java \
        src/test/java/com/roadmapsh/urlshortener/services/UrlShortenerServiceImplTest.java
git commit -m "feat: implement createShortUrl with validation and unique code generation"
```

---

## Task 7: Implement getOriginalUrl Service Method

**Files:**
- Modify: `src/main/java/com/roadmapsh/urlshortener/services/impl/UrlShortenerServiceImpl.java`
- Modify: `src/test/java/com/roadmapsh/urlshortener/services/UrlShortenerServiceImplTest.java`

- [ ] **Step 1: Add failing test for getOriginalUrl**

Add to `UrlShortenerServiceImplTest.java`:

```java
    @Test
    void getOriginalUrl_shouldReturnResponse_whenShortCodeExists() {
        // Arrange
        UrlShortener entity = new UrlShortener();
        entity.setShortCode("abc123");
        entity.setUrl("https://example.com");
        entity.setCreationDate(LocalDateTime.now());
        entity.setUpdatedDate(LocalDateTime.now());
        entity.setAccessedTimes(5);

        when(urlShortenerDAO.findByShortCode("abc123")).thenReturn(Optional.of(entity));
        when(urlShortenerDAO.save(any(UrlShortener.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Optional<UrlShortenerResponse> response = service.getOriginalUrl("abc123");

        // Assert
        assertTrue(response.isPresent());
        assertEquals("abc123", response.get().getShortCode());
        assertEquals("https://example.com", response.get().getUrl());
        
        ArgumentCaptor<UrlShortener> captor = ArgumentCaptor.forClass(UrlShortener.class);
        verify(urlShortenerDAO).save(captor.capture());
        assertEquals(6, captor.getValue().getAccessedTimes()); // Incremented from 5 to 6
    }

    @Test
    void getOriginalUrl_shouldReturnEmpty_whenShortCodeNotExists() {
        // Arrange
        when(urlShortenerDAO.findByShortCode("notexist")).thenReturn(Optional.empty());

        // Act
        Optional<UrlShortenerResponse> response = service.getOriginalUrl("notexist");

        // Assert
        assertTrue(response.isEmpty());
        verify(urlShortenerDAO, never()).save(any());
    }
```

Add import at top:

```java
import java.time.LocalDateTime;
import java.util.Optional;
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./mvnw test -Dtest=UrlShortenerServiceImplTest#getOriginalUrl_shouldReturnResponse_whenShortCodeExists -q`
Expected: FAIL - accessedTimes not incremented

- [ ] **Step 3: Implement getOriginalUrl with access count increment**

Update the method in `UrlShortenerServiceImpl.java`:

```java
    @Override
    @Transactional
    public Optional<UrlShortenerResponse> getOriginalUrl(String shortCode) {
        Optional<UrlShortener> optionalEntity = urlShortenerDAO.findByShortCode(shortCode);
        
        if (optionalEntity.isEmpty()) {
            return Optional.empty();
        }
        
        UrlShortener entity = optionalEntity.get();
        entity.setAccessedTimes(entity.getAccessedTimes() + 1);
        UrlShortener saved = urlShortenerDAO.save(entity);
        
        return Optional.of(UrlShortenerMapper.fromModelToDto(saved));
    }
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `./mvnw test -Dtest=UrlShortenerServiceImplTest -q`
Expected: 4 tests PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/roadmapsh/urlshortener/services/impl/UrlShortenerServiceImpl.java \
        src/test/java/com/roadmapsh/urlshortener/services/UrlShortenerServiceImplTest.java
git commit -m "feat: implement getOriginalUrl with access count tracking"
```

---

## Task 8: Implement updateShortUrl Service Method

**Files:**
- Modify: `src/main/java/com/roadmapsh/urlshortener/services/impl/UrlShortenerServiceImpl.java`
- Modify: `src/test/java/com/roadmapsh/urlshortener/services/UrlShortenerServiceImplTest.java`

- [ ] **Step 1: Add failing tests for updateShortUrl**

Add to `UrlShortenerServiceImplTest.java`:

```java
    @Test
    void updateShortUrl_shouldReturnUpdatedResponse_whenShortCodeExists() throws InvalidUrlException, UrlNotFoundException {
        // Arrange
        UrlShortener entity = new UrlShortener();
        entity.setShortCode("abc123");
        entity.setUrl("https://old-url.com");
        entity.setCreationDate(LocalDateTime.now().minusDays(1));
        entity.setUpdatedDate(LocalDateTime.now().minusDays(1));
        entity.setAccessedTimes(10);

        UrlShortenerRequest request = new UrlShortenerRequest();
        request.setUrl("https://new-url.com");

        when(urlShortenerDAO.findByShortCode("abc123")).thenReturn(Optional.of(entity));
        when(urlShortenerDAO.save(any(UrlShortener.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        UrlShortenerResponse response = service.updateShortUrl("abc123", request);

        // Assert
        assertEquals("abc123", response.getShortCode());
        assertEquals("https://new-url.com", response.getUrl());
        
        ArgumentCaptor<UrlShortener> captor = ArgumentCaptor.forClass(UrlShortener.class);
        verify(urlShortenerDAO).save(captor.capture());
        assertEquals("https://new-url.com", captor.getValue().getUrl());
        assertEquals(10, captor.getValue().getAccessedTimes()); // Should not change
    }

    @Test
    void updateShortUrl_shouldThrowUrlNotFoundException_whenShortCodeNotExists() {
        // Arrange
        UrlShortenerRequest request = new UrlShortenerRequest();
        request.setUrl("https://new-url.com");

        when(urlShortenerDAO.findByShortCode("notexist")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UrlNotFoundException.class, () -> service.updateShortUrl("notexist", request));
    }

    @Test
    void updateShortUrl_shouldThrowInvalidUrlException_whenNewUrlIsInvalid() {
        // Arrange
        UrlShortenerRequest request = new UrlShortenerRequest();
        request.setUrl("invalid-url");

        // Act & Assert
        assertThrows(InvalidUrlException.class, () -> service.updateShortUrl("abc123", request));
        verify(urlShortenerDAO, never()).findByShortCode(any());
    }
```

Add import:

```java
import com.roadmapsh.urlshortener.errors.UrlNotFoundException;
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./mvnw test -Dtest=UrlShortenerServiceImplTest#updateShortUrl_shouldReturnUpdatedResponse_whenShortCodeExists -q`
Expected: FAIL - RuntimeException "NOT IMPLEMENTED"

- [ ] **Step 3: Implement updateShortUrl**

Update the method in `UrlShortenerServiceImpl.java`:

```java
    @Override
    @Transactional
    public UrlShortenerResponse updateShortUrl(String shortCode, UrlShortenerRequest request) throws InvalidUrlException, UrlNotFoundException {
        UrlValidator.validate(request.getUrl());
        
        Optional<UrlShortener> optionalEntity = urlShortenerDAO.findByShortCode(shortCode);
        
        if (optionalEntity.isEmpty()) {
            throw new UrlNotFoundException("Short code not found: " + shortCode);
        }
        
        UrlShortener entity = optionalEntity.get();
        entity.setUrl(request.getUrl());
        entity.setUpdatedDate(LocalDateTime.now());
        
        UrlShortener saved = urlShortenerDAO.save(entity);
        return UrlShortenerMapper.fromModelToDto(saved);
    }
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `./mvnw test -Dtest=UrlShortenerServiceImplTest -q`
Expected: 7 tests PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/roadmapsh/urlshortener/services/impl/UrlShortenerServiceImpl.java \
        src/test/java/com/roadmapsh/urlshortener/services/UrlShortenerServiceImplTest.java
git commit -m "feat: implement updateShortUrl with validation"
```

---

## Task 9: Implement deleteShortUrl Service Method

**Files:**
- Modify: `src/main/java/com/roadmapsh/urlshortener/services/impl/UrlShortenerServiceImpl.java`
- Modify: `src/test/java/com/roadmapsh/urlshortener/services/UrlShortenerServiceImplTest.java`

- [ ] **Step 1: Add failing tests for deleteShortUrl**

Add to `UrlShortenerServiceImplTest.java`:

```java
    @Test
    void deleteShortUrl_shouldDelete_whenShortCodeExists() throws UrlNotFoundException {
        // Arrange
        when(urlShortenerDAO.existsById("abc123")).thenReturn(true);

        // Act
        service.deleteShortUrl("abc123");

        // Assert
        verify(urlShortenerDAO).deleteById("abc123");
    }

    @Test
    void deleteShortUrl_shouldThrowUrlNotFoundException_whenShortCodeNotExists() {
        // Arrange
        when(urlShortenerDAO.existsById("notexist")).thenReturn(false);

        // Act & Assert
        assertThrows(UrlNotFoundException.class, () -> service.deleteShortUrl("notexist"));
        verify(urlShortenerDAO, never()).deleteById(any());
    }
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./mvnw test -Dtest=UrlShortenerServiceImplTest#deleteShortUrl_shouldDelete_whenShortCodeExists -q`
Expected: FAIL - RuntimeException "NOT IMPLEMENTED"

- [ ] **Step 3: Implement deleteShortUrl**

Update the method in `UrlShortenerServiceImpl.java`:

```java
    @Override
    @Transactional
    public void deleteShortUrl(String shortCode) throws UrlNotFoundException {
        if (!urlShortenerDAO.existsById(shortCode)) {
            throw new UrlNotFoundException("Short code not found: " + shortCode);
        }
        urlShortenerDAO.deleteById(shortCode);
        log.info("Deleted short URL: {}", shortCode);
    }
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `./mvnw test -Dtest=UrlShortenerServiceImplTest -q`
Expected: 9 tests PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/roadmapsh/urlshortener/services/impl/UrlShortenerServiceImpl.java \
        src/test/java/com/roadmapsh/urlshortener/services/UrlShortenerServiceImplTest.java
git commit -m "feat: implement deleteShortUrl"
```

---

## Task 10: Implement getUrlStatistics Service Method

**Files:**
- Modify: `src/main/java/com/roadmapsh/urlshortener/services/impl/UrlShortenerServiceImpl.java`
- Modify: `src/test/java/com/roadmapsh/urlshortener/services/UrlShortenerServiceImplTest.java`

- [ ] **Step 1: Add failing tests for getUrlStatistics**

Add to `UrlShortenerServiceImplTest.java`:

```java
    @Test
    void getUrlStatistics_shouldReturnStatsResponse_whenShortCodeExists() {
        // Arrange
        UrlShortener entity = new UrlShortener();
        entity.setShortCode("abc123");
        entity.setUrl("https://example.com");
        entity.setCreationDate(LocalDateTime.now());
        entity.setUpdatedDate(LocalDateTime.now());
        entity.setAccessedTimes(42);

        when(urlShortenerDAO.findByShortCode("abc123")).thenReturn(Optional.of(entity));

        // Act
        Optional<UrlShortenerStatsResponse> response = service.getUrlStatistics("abc123");

        // Assert
        assertTrue(response.isPresent());
        assertEquals("abc123", response.get().getShortCode());
        assertEquals(42, response.get().getAccessCount());
    }

    @Test
    void getUrlStatistics_shouldReturnEmpty_whenShortCodeNotExists() {
        // Arrange
        when(urlShortenerDAO.findByShortCode("notexist")).thenReturn(Optional.empty());

        // Act
        Optional<UrlShortenerStatsResponse> response = service.getUrlStatistics("notexist");

        // Assert
        assertTrue(response.isEmpty());
    }
```

Add import:

```java
import com.roadmapsh.urlshortener.dtos.responses.UrlShortenerStatsResponse;
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./mvnw test -Dtest=UrlShortenerServiceImplTest#getUrlStatistics_shouldReturnStatsResponse_whenShortCodeExists -q`
Expected: FAIL - RuntimeException "NOT IMPLEMENTED"

- [ ] **Step 3: Implement getUrlStatistics**

Update the method in `UrlShortenerServiceImpl.java`:

```java
    @Override
    @Transactional(readOnly = true)
    public Optional<UrlShortenerStatsResponse> getUrlStatistics(String shortCode) {
        Optional<UrlShortener> optionalEntity = urlShortenerDAO.findByShortCode(shortCode);
        
        if (optionalEntity.isEmpty()) {
            return Optional.empty();
        }
        
        return Optional.of(UrlShortenerMapper.fromModelToStatsDto(optionalEntity.get()));
    }
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `./mvnw test -Dtest=UrlShortenerServiceImplTest -q`
Expected: 11 tests PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/roadmapsh/urlshortener/services/impl/UrlShortenerServiceImpl.java \
        src/test/java/com/roadmapsh/urlshortener/services/UrlShortenerServiceImplTest.java
git commit -m "feat: implement getUrlStatistics"
```

---

## Task 11: Add OptimisticLockException Handler to Controller

**Files:**
- Modify: `src/main/java/com/roadmapsh/urlshortener/controllers/UrlShortenerController.java`

- [ ] **Step 1: Add exception handler for OptimisticLockException**

Add to `UrlShortenerController.java`:

```java
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

Full updated controller:

```java
package com.roadmapsh.urlshortener.controllers;

import com.roadmapsh.urlshortener.dtos.requests.UrlShortenerRequest;
import com.roadmapsh.urlshortener.dtos.responses.ErrorResponse;
import com.roadmapsh.urlshortener.dtos.responses.UrlShortenerResponse;
import com.roadmapsh.urlshortener.dtos.responses.UrlShortenerStatsResponse;
import com.roadmapsh.urlshortener.errors.InvalidUrlException;
import com.roadmapsh.urlshortener.errors.UrlNotFoundException;
import com.roadmapsh.urlshortener.services.UrlShortenerService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/shorten")
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    private Logger log = LoggerFactory.getLogger(UrlShortenerController.class);

    public UrlShortenerController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

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

    @GetMapping("/{shortCode}")
    public ResponseEntity<UrlShortenerResponse> getOriginalUrl(@PathVariable String shortCode) {
        Optional<UrlShortenerResponse> response = urlShortenerService.getOriginalUrl(shortCode);
        return response.map(urlResponse -> new ResponseEntity<>(urlResponse, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{shortCode}")
    public ResponseEntity<UrlShortenerResponse> updateShortUrl(@PathVariable String shortCode, @RequestBody UrlShortenerRequest request) {
        try {
            UrlShortenerResponse response = urlShortenerService.updateShortUrl(shortCode, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (InvalidUrlException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (UrlNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteShortUrl(@PathVariable String shortCode) {
        try {
            urlShortenerService.deleteShortUrl(shortCode);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (UrlNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{shortCode}/stats")
    public ResponseEntity<UrlShortenerStatsResponse> getUrlStatistics(@PathVariable String shortCode) {
        Optional<UrlShortenerStatsResponse> response = urlShortenerService.getUrlStatistics(shortCode);
        return response.map(statsResponse -> new ResponseEntity<>(statsResponse, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

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
}
```

- [ ] **Step 2: Verify application compiles**

Run: `./mvnw compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/roadmapsh/urlshortener/controllers/UrlShortenerController.java
git commit -m "feat: add OptimisticLockException handler for 409 Conflict responses"
```

---

## Task 12: Run All Tests and Verify Build

**Files:** None (verification only)

- [ ] **Step 1: Run all unit tests**

Run: `./mvnw test -q`
Expected: All tests PASS

- [ ] **Step 2: Run full build**

Run: `./mvnw clean package -DskipTests -q`
Expected: BUILD SUCCESS

- [ ] **Step 3: Final commit (if any uncommitted changes)**

```bash
git status
# If there are uncommitted changes:
git add -A
git commit -m "chore: complete URL shortener core API implementation"
```

---

## Summary

| Task | Description | Files |
|------|-------------|-------|
| 1 | URL Validator utility | utils/UrlValidator.java, tests |
| 2 | Update InvalidUrlException | errors/InvalidUrlException.java |
| 3 | Short Code Generator utility | utils/ShortCodeGenerator.java, tests |
| 4 | Add @Version to model | models/UrlShortener.java |
| 5 | Update mapper | mappers/UrlShortenerMapper.java |
| 6 | Implement createShortUrl | services/impl/UrlShortenerServiceImpl.java |
| 7 | Implement getOriginalUrl | services/impl/UrlShortenerServiceImpl.java |
| 8 | Implement updateShortUrl | services/impl/UrlShortenerServiceImpl.java |
| 9 | Implement deleteShortUrl | services/impl/UrlShortenerServiceImpl.java |
| 10 | Implement getUrlStatistics | services/impl/UrlShortenerServiceImpl.java |
| 11 | Add conflict handler | controllers/UrlShortenerController.java |
| 12 | Verify build | - |
