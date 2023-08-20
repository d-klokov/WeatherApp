package ru.klokov.dto.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherApiResponse {
    @JsonProperty("coord")
    private Coordinate coordinate;
    @JsonProperty("weather")
    private List<Weather> weather;
    @JsonProperty("main")
    private Main main;
    @JsonProperty("wind")
    private Wind wind;
    @JsonProperty("clouds")
    private Clouds clouds;
    @JsonProperty("dt")
    private Long dateTime;
    @JsonProperty("name")
    private String locationName;
}
