package com.roadmapsh.urlshortener.services;

import com.roadmapsh.urlshortener.dtos.requests.UrlShortenerRequest;
import com.roadmapsh.urlshortener.dtos.responses.UrlShortenerResponse;
import com.roadmapsh.urlshortener.dtos.responses.UrlShortenerStatsResponse;
import com.roadmapsh.urlshortener.errors.InvalidUrlException;
import com.roadmapsh.urlshortener.errors.UrlNotFoundException;

import java.util.Optional;

public interface UrlShortenerService {


    /**
     * Create a short URL for the given URL.
     *
     * @param request the URL to shorten
     * @return UrlShortenerResponse with the shortened URL
     * @throws InvalidUrlException if the URL is invalid
     */
    UrlShortenerResponse createShortUrl(UrlShortenerRequest request) throws InvalidUrlException;

    /**
     * Retrieve the original URL for the given short code.
     *
     * @param shortCode the short code
     * @return UrlShortenerResponse with the original URL
     */
    Optional<UrlShortenerResponse> getOriginalUrl(String shortCode);

    /**
     * Update the URL for the given short code.
     *
     * @param shortCode the short code
     * @param request the new URL
     * @return UrlShortenerResponse with the updated URL
     * @throws InvalidUrlException if the URL is invalid
     * @throws UrlNotFoundException if the short code is not found
     */
    UrlShortenerResponse updateShortUrl(String shortCode, UrlShortenerRequest request) throws InvalidUrlException, UrlNotFoundException;

    /**
     * Delete the short URL for the given short code.
     *
     * @param shortCode the short code
     * @throws UrlNotFoundException if the short code is not found
     */
    void deleteShortUrl(String shortCode) throws UrlNotFoundException;

    /**
     * Retrieve the statistics for the given short code.
     *
     * @param shortCode the short code
     * @return UrlShortenerStatsResponse with the URL statistics
     */
    Optional<UrlShortenerStatsResponse> getUrlStatistics(String shortCode);
}
