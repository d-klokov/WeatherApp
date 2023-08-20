package ru.klokov.exception;

public class GeoCodingApiException extends RuntimeException {
    public GeoCodingApiException(String message) {
        super(message);
    }
}
