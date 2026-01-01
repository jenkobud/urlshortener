package com.roadmapsh.urlshortener.daos;

import com.roadmapsh.urlshortener.models.UrlShortener;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlShortenerDAO extends JpaRepository<UrlShortener, String> {
    Optional<UrlShortener> findByShortCode(String shortCode);
    Optional<UrlShortener> findByUrl(String url);
}
