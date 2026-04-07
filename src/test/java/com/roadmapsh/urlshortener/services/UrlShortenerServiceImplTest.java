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
}
