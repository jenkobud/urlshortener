package com.roadmapsh.urlshortener.daos;

import com.roadmapsh.urlshortener.models.UrlShortener;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlShortenerDAO extends JpaRepository<UrlShortener, Long> {
    UrlShortener create(String url);
    UrlShortener findByShortCode(String shortCode);
    UrlShortener update(String url);
    boolean delete(String shortCode);
    UrlShortener findByUrl(String url);
}
