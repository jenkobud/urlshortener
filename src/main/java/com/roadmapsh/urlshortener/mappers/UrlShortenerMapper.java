package com.roadmapsh.urlshortener.mappers;

import com.roadmapsh.urlshortener.dtos.responses.UrlShortenerResponse;
import com.roadmapsh.urlshortener.models.UrlShortener;
import jakarta.validation.constraints.NotNull;

public final class UrlShortenerMapper {

    private UrlShortenerMapper() {
        // Private constructor to prevent instantiation
    }

    public static UrlShortenerResponse fromModelToDto(@NotNull UrlShortener urlShortener) {
        UrlShortenerResponse dto = new UrlShortenerResponse();
        dto.setShortCode(urlShortener.getShortCode());
        dto.setUrl(urlShortener.getUrl());
        dto.setCreatedAt(urlShortener.getCreationDate());
        dto.setUpdatedAt(urlShortener.getUpdatedDate());
        return dto;
    }
}
