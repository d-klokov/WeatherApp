package ru.klokov.exception.openweathermap;

public class OpenWeatherMapApiInvalidApiKeyException extends RuntimeException {
    public OpenWeatherMapApiInvalidApiKeyException(String message) {
        super(message);
    }
}
