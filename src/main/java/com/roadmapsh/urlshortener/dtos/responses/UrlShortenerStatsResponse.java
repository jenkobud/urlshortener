package com.roadmapsh.urlshortener.dtos.responses;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UrlShortenerStatsResponse extends UrlShortenerResponse {
    private Number accessCount = 0;
}
