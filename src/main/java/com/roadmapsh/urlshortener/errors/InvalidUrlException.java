package com.roadmapsh.urlshortener.errors;

public class InvalidUrlException extends Exception {
    public InvalidUrlException(String message) {
        super(message);
    }
}
