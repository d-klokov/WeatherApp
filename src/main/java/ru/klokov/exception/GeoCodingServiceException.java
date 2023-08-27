package ru.klokov.exception;

public class GeoCodingServiceException extends RuntimeException {
    public GeoCodingServiceException(String message) {
        super(message);
    }
}
