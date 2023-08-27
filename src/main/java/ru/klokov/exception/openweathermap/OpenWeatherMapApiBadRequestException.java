package ru.klokov.exception.openweathermap;

public class OpenWeatherMapApiBadRequestException extends RuntimeException {
    public OpenWeatherMapApiBadRequestException(String message) {
        super(message);
    }
}
