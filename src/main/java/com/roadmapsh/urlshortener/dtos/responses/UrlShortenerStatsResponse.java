package com.roadmapsh.urlshortener.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Schema(description = "Response containing short URL details with access statistics")
public class UrlShortenerStatsResponse extends UrlShortenerResponse {
    @Schema(description = "Number of times this short URL has been accessed", example = "10")
    private Number accessCount = 0;
}
