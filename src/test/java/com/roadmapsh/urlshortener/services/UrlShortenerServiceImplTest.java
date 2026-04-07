package com.roadmapsh.urlshortener.services;

import com.roadmapsh.urlshortener.daos.UrlShortenerDAO;
import com.roadmapsh.urlshortener.dtos.requests.UrlShortenerRequest;
import com.roadmapsh.urlshortener.dtos.responses.UrlShortenerResponse;
import com.roadmapsh.urlshortener.errors.InvalidUrlException;
import com.roadmapsh.urlshortener.errors.UrlNotFoundException;
import com.roadmapsh.urlshortener.models.UrlShortener;
import com.roadmapsh.urlshortener.services.impl.UrlShortenerServiceImpl;
import com.roadmapsh.urlshortener.utils.ShortCodeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

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

    @Test
    void createShortUrl_shouldRetryOnCollision() throws InvalidUrlException {
        // Arrange
        UrlShortenerRequest request = new UrlShortenerRequest();
        request.setUrl("https://example.com/test");
        
        when(shortCodeGenerator.generate()).thenReturn("abc123", "def456");
        when(urlShortenerDAO.existsById("abc123")).thenReturn(true);  // First collision
        when(urlShortenerDAO.existsById("def456")).thenReturn(false);  // Second attempt succeeds
        when(urlShortenerDAO.save(any(UrlShortener.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        UrlShortenerResponse response = service.createShortUrl(request);

        // Assert
        assertEquals("def456", response.getShortCode());
        verify(shortCodeGenerator, times(2)).generate();
    }

    @Test
    void createShortUrl_shouldThrowException_afterMaxRetries() {
        // Arrange
        UrlShortenerRequest request = new UrlShortenerRequest();
        request.setUrl("https://example.com/test");
        
        when(shortCodeGenerator.generate()).thenReturn("abc123", "def456", "ghi789");
        when(urlShortenerDAO.existsById(any())).thenReturn(true);  // All collide

        // Act & Assert
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.createShortUrl(request));
        assertTrue(ex.getMessage().contains("Failed to generate unique short code"));
        verify(shortCodeGenerator, times(3)).generate();
        verify(urlShortenerDAO, never()).save(any());
    }

    @Test
    void getOriginalUrl_shouldReturnResponseAndIncrementCounter_whenShortCodeExists() {
        // Arrange
        String shortCode = "abc123";
        UrlShortener entity = new UrlShortener();
        entity.setShortCode(shortCode);
        entity.setUrl("https://example.com/original");
        entity.setCreationDate(LocalDateTime.now());
        entity.setUpdatedDate(LocalDateTime.now());
        entity.setAccessedTimes(5);
        
        when(urlShortenerDAO.findById(shortCode)).thenReturn(Optional.of(entity));
        when(urlShortenerDAO.save(any(UrlShortener.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Optional<UrlShortenerResponse> result = service.getOriginalUrl(shortCode);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("https://example.com/original", result.get().getUrl());
        assertEquals(shortCode, result.get().getShortCode());
        
        // Verify accessedTimes was incremented
        ArgumentCaptor<UrlShortener> captor = ArgumentCaptor.forClass(UrlShortener.class);
        verify(urlShortenerDAO).save(captor.capture());
        assertEquals(6, captor.getValue().getAccessedTimes());
    }

    @Test
    void getOriginalUrl_shouldReturnEmpty_whenShortCodeDoesNotExist() {
        // Arrange
        String shortCode = "nonexistent";
        when(urlShortenerDAO.findById(shortCode)).thenReturn(Optional.empty());

        // Act
        Optional<UrlShortenerResponse> result = service.getOriginalUrl(shortCode);

        // Assert
        assertFalse(result.isPresent());
        verify(urlShortenerDAO, never()).save(any());
    }

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

        when(urlShortenerDAO.findById("abc123")).thenReturn(Optional.of(entity));
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

        when(urlShortenerDAO.findById("notexist")).thenReturn(Optional.empty());

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
        verify(urlShortenerDAO, never()).findById(any());
    }

    @Test
    void deleteShortUrl_shouldDeleteSuccessfully_whenShortCodeExists() throws UrlNotFoundException {
        // Arrange
        String shortCode = "abc123";
        when(urlShortenerDAO.existsById(shortCode)).thenReturn(true);

        // Act
        service.deleteShortUrl(shortCode);

        // Assert
        verify(urlShortenerDAO).existsById(shortCode);
        verify(urlShortenerDAO).deleteById(shortCode);
    }

    @Test
    void deleteShortUrl_shouldThrowUrlNotFoundException_whenShortCodeDoesNotExist() {
        // Arrange
        String shortCode = "nonexistent";
        when(urlShortenerDAO.existsById(shortCode)).thenReturn(false);

        // Act & Assert
        assertThrows(UrlNotFoundException.class, () -> service.deleteShortUrl(shortCode));
        verify(urlShortenerDAO).existsById(shortCode);
        verify(urlShortenerDAO, never()).deleteById(any());
    }
}
