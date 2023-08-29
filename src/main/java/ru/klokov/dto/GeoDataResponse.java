package ru.klokov.dto;

import lombok.Data;

@Data
public class GeoDataResponse {
    private String name;
    private String latitude;
    private String longitude;
    private String country;
}
