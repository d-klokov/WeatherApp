package ru.klokov.exception;

public class LoadingPropertiesFailedException extends RuntimeException {
    public LoadingPropertiesFailedException() {
        super();
    }

    public LoadingPropertiesFailedException(String message) {
        super(message);
    }
}
