package ru.klokov.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.klokov.dto.weather.WeatherApiResponse;
import ru.klokov.dto.weather.WeatherResponse;
import ru.klokov.model.Location;
import ru.klokov.util.OpenWeatherMapUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class WeatherService {
    private final ObjectMapper mapper;

    public WeatherService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public WeatherApiResponse getWeatherDataByLocation(Location location) throws ExecutionException, InterruptedException, JsonProcessingException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(getWeatherApiUri(location)).build();

        CompletableFuture<String> weatherApiResponseFuture = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);

        String weatherApiResponseJson = weatherApiResponseFuture.get();

        System.out.println(weatherApiResponseJson);

        return mapper.readValue(weatherApiResponseJson, WeatherApiResponse.class);
    }

    public WeatherResponse getWeatherResponse(WeatherApiResponse weatherApiResponse) {
        WeatherResponse weatherResponse = new WeatherResponse();
        weatherResponse.setLocationName(weatherApiResponse.getLocationName());
        weatherResponse.setCurrentTime(getCurrentTime(weatherApiResponse.getDateTime()));
        weatherResponse.setTemperature(Math.round(weatherApiResponse.getMain().getTemperature()));
        weatherResponse.setIcon(weatherApiResponse.getWeather().get(0).getIcon());
        weatherResponse.setDescription(weatherApiResponse.getWeather().get(0).getDescription());
        weatherResponse.setFeelsLike(Math.round(weatherApiResponse.getMain().getFeelsLike()));
        weatherResponse.setWindSpeed(weatherApiResponse.getWind().getSpeed());
        weatherResponse.setWindDirection(getWindDirection(weatherApiResponse.getWind().getDeg()));
        weatherResponse.setHumidity(weatherApiResponse.getMain().getHumidity());
        weatherResponse.setPressure(weatherApiResponse.getMain().getPressure());

        return weatherResponse;
    }

    private String getCurrentTime(Long dateTime) {
        LocalTime localTime = LocalTime.ofInstant(Instant.ofEpochSecond(dateTime), ZoneId.systemDefault());
        int hour = localTime.getHour();
        int minute = localTime.getMinute();

        String hours = hour < 10 ? "0" + hour : String.valueOf(hour);
        String minutes = minute < 10 ? "0" + minute : String.valueOf(minute);
        return hours + ":" + minutes;
    }

    private String getWindDirection(Integer deg) {
        if ((deg > 0 && deg < 22.5) || (deg > 337.5)) return "N";
        else if (deg >= 22.5 && deg < 67.5) return "NE";
        else if (deg >= 67.5 && deg < 112.5) return "E";
        else if (deg >= 112.5 && deg < 157.5) return "SE";
        else if (deg >= 157.5 && deg < 202.5) return "S";
        else if (deg >= 202.5 && deg < 247.5) return "SW";
        else if (deg >= 247.5 && deg < 292.5) return "W";
        else if (deg >= 292.5 && deg < 337.5) return "NW";
        else throw new RuntimeException();
    }

    private URI getWeatherApiUri(Location location) {
        String uriString = OpenWeatherMapUtil.getBaseUrl() +
                OpenWeatherMapUtil.getCurrentWeatherApiUrl() +
                "?lat=" + location.getLatitude() +
                "&lon=" + location.getLongitude() +
                "&appid=" + OpenWeatherMapUtil.getApiKey() +
                "&units=metric";

        return URI.create(uriString);
    }
}