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

    @Test
    void validate_shouldThrowException_whenUrlIsInvalidFormat() {
        InvalidUrlException ex = assertThrows(InvalidUrlException.class, 
            () -> UrlValidator.validate("not a valid url"));
        assertEquals("URL_INVALID_FORMAT", ex.getErrorCode());
    }

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
}
