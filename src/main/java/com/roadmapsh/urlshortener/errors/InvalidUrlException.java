package com.roadmapsh.urlshortener.errors;

import lombok.Getter;

public class InvalidUrlException extends Exception {

    @Getter
    private final String errorCode = "INVALID_URL";
    public InvalidUrlException(String message) {
        super(message);
    }
}
