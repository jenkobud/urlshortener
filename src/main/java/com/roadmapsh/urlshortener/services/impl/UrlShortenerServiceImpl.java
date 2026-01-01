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
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class UrlShortenerServiceImpl implements UrlShortenerService {

    private final UrlShortenerDAO urlShortenerDAO;

    public UrlShortenerServiceImpl(UrlShortenerDAO urlShortenerDAO) {
        this.urlShortenerDAO = urlShortenerDAO;
    }

    @Override
    public UrlShortenerResponse createShortUrl(UrlShortenerRequest request) throws InvalidUrlException {
        //TODO Implement URL validation and short code generation logic
        String hash = request.getUrl().hashCode() + "";
        log.info("Shortening URL: {} to: {}", request.getUrl(), hash);
        UrlShortenerResponse response = new UrlShortenerResponse();
        response.setId("0");
        response.setShortCode(hash);
        response.setUrl(request.getUrl());
        response.setCreatedAt(LocalDateTime.now());

        return response;
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