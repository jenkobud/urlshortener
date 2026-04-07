package com.roadmapsh.urlshortener.dtos.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Schema(description = "Request body for creating or updating a short URL")
public class UrlShortenerRequest {
    @NotNull
    @NotEmpty
    @Schema(description = "The URL to shorten", example = "https://www.example.com/some/long/url")
    private String url;
}
