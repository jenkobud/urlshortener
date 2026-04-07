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
        // TODO - Implement URL validation and update logic
        // Return the updated UrlShortenerResponse
        throw new RuntimeException("NOT IMPLEMENTE");
    }
    @Override
    public void deleteShortUrl(String shortCode) throws UrlNotFoundException {
        // TODO - Implement logic to delete the short URL by short code
        throw new RuntimeException("NOT IMPLEMENTE");
    }
    @Override
    public Optional<UrlShortenerStatsResponse> getUrlStatistics(String shortCode) {
        // TODO - IAutowired mplement logic to retrieve URL statistics by short code
        throw new RuntimeException("NOT IMPLEMENTE");
    }
}