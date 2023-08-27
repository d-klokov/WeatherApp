package ru.klokov.exception.openweathermap;

public class OpenWeatherMapApiInternalErrorException extends RuntimeException {
    public OpenWeatherMapApiInternalErrorException(String message) {
        super(message);
    }
}
