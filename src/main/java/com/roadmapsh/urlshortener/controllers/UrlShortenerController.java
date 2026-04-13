package com.roadmapsh.urlshortener.controllers;

import com.roadmapsh.urlshortener.dtos.requests.UrlShortenerRequest;
import com.roadmapsh.urlshortener.dtos.responses.ErrorResponse;
import com.roadmapsh.urlshortener.dtos.responses.UrlShortenerResponse;
import com.roadmapsh.urlshortener.dtos.responses.UrlShortenerStatsResponse;
import com.roadmapsh.urlshortener.errors.InvalidUrlException;
import com.roadmapsh.urlshortener.errors.UrlNotFoundException;
import com.roadmapsh.urlshortener.services.UrlShortenerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Tag(name = "URL Shortener", description = "Operations for shortening and managing URLs")
@RestController
@RequestMapping("/shorten")
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    private Logger log = LoggerFactory.getLogger(UrlShortenerController.class);

    public UrlShortenerController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @Operation(summary = "Create a new short URL", description = "Creates a shortened URL for the provided long URL")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Short URL created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UrlShortenerResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid URL provided",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<Object> createShortUrl(@RequestBody @Valid UrlShortenerRequest request) {
        log.info("Starting shortening of {}", request.getUrl());
        try {
            UrlShortenerResponse response = urlShortenerService.createShortUrl(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (InvalidUrlException e) {
            ErrorResponse err = new ErrorResponse(LocalDateTime.now(), 400, e.getErrorCode() , e.getMessage(), "/shorten");
            return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Retrieve original URL by short code", description = "Returns the original URL and increments the access counter")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "URL found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UrlShortenerResponse.class))),
        @ApiResponse(responseCode = "404", description = "Short URL not found")
    })
    @GetMapping("/{shortCode}")
    public ResponseEntity<UrlShortenerResponse> getOriginalUrl(
            @Parameter(description = "The short code to look up", example = "abc123") @PathVariable String shortCode) {
        Optional<UrlShortenerResponse> response = urlShortenerService.getOriginalUrl(shortCode);
        return response.map(urlResponse -> new ResponseEntity<>(urlResponse, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Update destination URL", description = "Updates the original URL for an existing short code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "URL updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UrlShortenerResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid URL provided",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Short URL not found")
    })
    @PutMapping("/{shortCode}")
    public ResponseEntity<UrlShortenerResponse> updateShortUrl(
            @Parameter(description = "The short code to update", example = "abc123") @PathVariable String shortCode,
            @RequestBody UrlShortenerRequest request) {
        try {
            UrlShortenerResponse response = urlShortenerService.updateShortUrl(shortCode, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (InvalidUrlException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (UrlNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Delete a short URL", description = "Permanently removes a short URL")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "URL deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Short URL not found")
    })
    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteShortUrl(
            @Parameter(description = "The short code to delete", example = "abc123") @PathVariable String shortCode) {
        try {
            urlShortenerService.deleteShortUrl(shortCode);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (UrlNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get URL access statistics", description = "Returns the short URL details including access count")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UrlShortenerStatsResponse.class))),
        @ApiResponse(responseCode = "404", description = "Short URL not found")
    })
    @GetMapping("/{shortCode}/stats")
    public ResponseEntity<UrlShortenerStatsResponse> getUrlStatistics(
            @Parameter(description = "The short code to get statistics for", example = "abc123") @PathVariable String shortCode) {
        Optional<UrlShortenerStatsResponse> response = urlShortenerService.getUrlStatistics(shortCode);
        return response.map(statsResponse -> new ResponseEntity<>(statsResponse, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(hidden = true)
    @ExceptionHandler(jakarta.persistence.OptimisticLockException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockException(jakarta.persistence.OptimisticLockException e) {
        ErrorResponse err = new ErrorResponse(
            LocalDateTime.now(), 
            409, 
            "CONFLICT", 
            "Resource was modified by another request. Please retry.", 
            "/shorten"
        );
        return new ResponseEntity<>(err, HttpStatus.CONFLICT);
    }
}
