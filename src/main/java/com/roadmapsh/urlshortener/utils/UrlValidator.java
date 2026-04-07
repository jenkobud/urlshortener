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
