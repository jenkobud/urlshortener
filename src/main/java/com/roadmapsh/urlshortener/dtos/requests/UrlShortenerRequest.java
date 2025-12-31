package com.roadmapsh.urlshortener.dtos.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UrlShortenerRequest {
    @NotNull
    @NotEmpty
    private String url;
}
