package ru.klokov.dto.weather;

import lombok.Data;

import java.time.LocalTime;

@Data
public class WeatherResponse {
    private String locationName;
    private Long locationId;
    private String currentTime;
    private Long temperature;
    private String icon;
    private String description;
    private Long feelsLike;
    private Double windSpeed;
    private String windDirection;
    private Integer humidity;
    private Integer pressure;
    private Double minTemperature;
    private Double maxTemperature;
    private Integer cloudiness;
}
