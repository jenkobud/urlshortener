package com.roadmapsh.urlshortener.errors;

import lombok.Getter;

public class InvalidUrlException extends Exception {

    @Getter
    private final String errorCode;

    public InvalidUrlException(String message) {
        super(message);
        this.errorCode = "INVALID_URL";
    }

    public InvalidUrlException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
