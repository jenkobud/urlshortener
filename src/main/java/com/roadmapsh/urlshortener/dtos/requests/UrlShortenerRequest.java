package com.roadmapsh.urlshortener.dtos.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UrlShortenerRequest {
    private String url;
}
