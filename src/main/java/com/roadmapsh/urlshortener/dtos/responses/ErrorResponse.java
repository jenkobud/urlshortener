package com.roadmapsh.urlshortener.dtos.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Schema(description = "Error response returned when a request fails")
public class ErrorResponse {
    @Schema(description = "Timestamp when the error occurred", example = "2021-09-01T12:00:00")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Error code", example = "URL_INVALID")
    private String error;

    @Schema(description = "Human-readable error message", example = "URL cannot be empty")
    private String message;

    @Schema(description = "Request path that caused the error", example = "/shorten")
    private String path;
}
