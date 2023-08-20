package ru.klokov.dto.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Main {
    @JsonProperty("temp")
    private Double temperature;
    @JsonProperty("feels_like")
    private Double feelsLike;
    @JsonProperty("pressure")
    private Integer pressure;
    @JsonProperty("humidity")
    private Integer humidity;
    @JsonProperty("temp_min")
    private Double minTemperature;
    @JsonProperty("temp_max")
    private Double maxTemperature;
}
