package com.roadmapsh.urlshortener.controllers;

import com.roadmapsh.urlshortener.dtos.requests.UrlShortenerRequest;
import com.roadmapsh.urlshortener.dtos.responses.UrlShortenerResponse;
import com.roadmapsh.urlshortener.dtos.responses.UrlShortenerStatsResponse;
import com.roadmapsh.urlshortener.errors.InvalidUrlException;
import com.roadmapsh.urlshortener.errors.UrlNotFoundException;
import com.roadmapsh.urlshortener.services.UrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/shorten")
public class UrlShortenerController {

    @Autowired private UrlShortenerService urlShortenerService;

    @PostMapping
    public ResponseEntity<UrlShortenerResponse> createShortUrl(@RequestBody UrlShortenerRequest request) {
        try {
            UrlShortenerResponse response = urlShortenerService.createShortUrl(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (InvalidUrlException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<UrlShortenerResponse> getOriginalUrl(@PathVariable String shortCode) {
        Optional<UrlShortenerResponse> response = urlShortenerService.getOriginalUrl(shortCode);
        return response.map(urlResponse -> new ResponseEntity<>(urlResponse, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{shortCode}")
    public ResponseEntity<UrlShortenerResponse> updateShortUrl(@PathVariable String shortCode, @RequestBody UrlShortenerRequest request) {
        try {
            UrlShortenerResponse response = urlShortenerService.updateShortUrl(shortCode, request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (InvalidUrlException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (UrlNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteShortUrl(@PathVariable String shortCode) {
        try {
            urlShortenerService.deleteShortUrl(shortCode);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (UrlNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{shortCode}/stats")
    public ResponseEntity<UrlShortenerStatsResponse> getUrlStatistics(@PathVariable String shortCode) {
        Optional<UrlShortenerStatsResponse> response = urlShortenerService.getUrlStatistics(shortCode);
        return response.map(statsResponse -> new ResponseEntity<>(statsResponse, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}