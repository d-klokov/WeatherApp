package ru.klokov.exception;

public class LocationAlreadyExistsException extends RuntimeException {
    public LocationAlreadyExistsException(String message) {
        super(message);
    }
}
