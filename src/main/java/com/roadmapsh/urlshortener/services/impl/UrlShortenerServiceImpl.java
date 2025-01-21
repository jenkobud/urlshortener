package com.roadmapsh.urlshortener.services.impl;

import com.roadmapsh.urlshortener.dtos.requests.UrlShortenerRequest;
import com.roadmapsh.urlshortener.dtos.responses.UrlShortenerResponse;
import com.roadmapsh.urlshortener.dtos.responses.UrlShortenerStatsResponse;
import com.roadmapsh.urlshortener.errors.InvalidUrlException;
import com.roadmapsh.urlshortener.errors.UrlNotFoundException;
import com.roadmapsh.urlshortener.services.UrlShortenerService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UrlShortenerServiceImpl implements UrlShortenerService {
    @Override
    public UrlShortenerResponse createShortUrl(UrlShortenerRequest request) throws InvalidUrlException {
        // Implement URL validation and short code generation logic
        // Return the created UrlShortenerResponse
        throw new RuntimeException("NOT IMPLEMENTE");
    }
    @Override
    public Optional<UrlShortenerResponse> getOriginalUrl(String shortCode) {
        // Implement logic to retrieve the original URL by short code
        throw new RuntimeException("NOT IMPLEMENTE");
    }
    @Override
    public UrlShortenerResponse updateShortUrl(String shortCode, UrlShortenerRequest request) throws InvalidUrlException, UrlNotFoundException {
        // Implement URL validation and update logic
        // Return the updated UrlShortenerResponse
        throw new RuntimeException("NOT IMPLEMENTE");
    }
    @Override
    public void deleteShortUrl(String shortCode) throws UrlNotFoundException {
        // Implement logic to delete the short URL by short code
        throw new RuntimeException("NOT IMPLEMENTE");
    }
    @Override
    public Optional<UrlShortenerStatsResponse> getUrlStatistics(String shortCode) {
        // Implement logic to retrieve URL statistics by short code
        throw new RuntimeException("NOT IMPLEMENTE");
    }
}