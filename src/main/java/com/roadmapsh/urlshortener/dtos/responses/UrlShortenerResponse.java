package com.roadmapsh.urlshortener.dtos.responses;

import java.time.LocalDateTime;

public class UrlShortenerResponse {
    protected String id;
    protected String url;
    protected String shortCode;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getShortCode() { return shortCode; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "UrlShortenerResponse{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", shortCode='" + shortCode + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
