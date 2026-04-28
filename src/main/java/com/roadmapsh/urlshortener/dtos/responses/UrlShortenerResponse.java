package com.roadmapsh.urlshortener.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Schema(description = "Response containing short URL details")
public class UrlShortenerResponse {
    @Schema(description = "Unique identifier (same as shortCode)", example = "abc123")
    protected String id;

    @Schema(description = "The original URL", example = "https://www.example.com/some/long/url")
    protected String url;

    @Schema(description = "The generated short code", example = "abc123")
    protected String shortCode;

    @Schema(description = "Timestamp when the short URL was created", example = "2021-09-01T12:00:00")
    protected LocalDateTime createdAt;

    @Schema(description = "Timestamp when the short URL was last updated", example = "2021-09-01T12:00:00")
    protected LocalDateTime updatedAt;
}
