package com.roadmapsh.urlshortener.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class UrlShortener {
    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String url;
    private String shortCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int accessCount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getShortCode() { return shortCode; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public int getAccessCount() { return accessCount; }
    public void setAccessCount(int accessCount) { this.accessCount = accessCount; }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UrlShortener{");
        sb.append("id='").append(id).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", shortCode='").append(shortCode).append('\'');
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", accessCount=").append(accessCount);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        UrlShortener that = (UrlShortener) o;
        return accessCount == that.accessCount && id.equals(that.id) && url.equals(that.url) && shortCode.equals(that.shortCode) && createdAt.equals(that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + url.hashCode();
        result = 31 * result + shortCode.hashCode();
        result = 31 * result + createdAt.hashCode();
        result = 31 * result + Objects.hashCode(updatedAt);
        result = 31 * result + accessCount;
        return result;
    }
}
