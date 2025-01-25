package com.roadmapsh.urlshortener.dtos.responses;

public class UrlShortenerStatsResponse extends UrlShortenerResponse {
    private Number accessCount = 0;

    public Number getAccessCount() { return accessCount; }
    public void setAccessCount(Number accessCount) { this.accessCount = accessCount; }

    @Override
    public String toString() {
        return "UrlShortenerStatsResponse{" +
                "accessCount=" + accessCount +
                ", id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", shortCode='" + shortCode + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
