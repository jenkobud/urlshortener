package com.roadmapsh.urlshortener.dtos.responses;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class UrlShortenerResponse {
    protected String id;
    protected String url;
    protected String shortCode;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
}
