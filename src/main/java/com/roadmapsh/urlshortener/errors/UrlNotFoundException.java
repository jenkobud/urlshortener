package com.roadmapsh.urlshortener.errors;

public class UrlNotFoundException extends Exception {
    public UrlNotFoundException(String message) {
        super(message);
    }
}
