package ru.klokov.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.thymeleaf.util.StringUtils;
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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class WeatherApiService {
    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public WeatherApiService(HttpClient httpClient, ObjectMapper mapper) {
        this.httpClient = httpClient;
        this.mapper = mapper;
    }

    public WeatherApiResponse getWeatherDataByLocation(Location location) throws ExecutionException, InterruptedException, JsonProcessingException, TimeoutException {
        HttpRequest request = HttpRequest.newBuilder().uri(getWeatherApiUri(location)).build();
        CompletableFuture<HttpResponse<String>> responseFuture = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .orTimeout(1, TimeUnit.MINUTES);
        HttpResponse<String> response = responseFuture.get();

        OpenWeatherMapUtil.checkStatusCode(response.statusCode());

        String weatherApiResponseJson = response.body();

        return mapper.readValue(weatherApiResponseJson, WeatherApiResponse.class);
    }

    public WeatherResponse getWeatherResponse(Location location, WeatherApiResponse weatherApiResponse) {
        WeatherResponse weatherResponse = new WeatherResponse();
        weatherResponse.setLocationId(location.getId());
        weatherResponse.setLocationName(location.getName());
        weatherResponse.setCurrentTime(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        weatherResponse.setTemperature(Math.round(weatherApiResponse.getMain().getTemperature()));
        weatherResponse.setIcon(weatherApiResponse.getWeather().get(0).getIcon());
        weatherResponse.setDescription(StringUtils.capitalize(weatherApiResponse.getWeather().get(0).getDescription()));
        weatherResponse.setFeelsLike(Math.round(weatherApiResponse.getMain().getFeelsLike()));
        weatherResponse.setWindSpeed(weatherApiResponse.getWind().getSpeed());
        weatherResponse.setWindDirection(getWindDirection(weatherApiResponse.getWind().getDeg()));
        weatherResponse.setHumidity(weatherApiResponse.getMain().getHumidity());
        weatherResponse.setPressure(weatherApiResponse.getMain().getPressure());
        weatherResponse.setMinTemperature(weatherApiResponse.getMain().getMinTemperature());
        weatherResponse.setMaxTemperature(weatherApiResponse.getMain().getMaxTemperature());
        weatherResponse.setCloudiness(weatherApiResponse.getClouds().getAll());

        return weatherResponse;
    }

    private String getWindDirection(Integer deg) {
        if ((deg >= 0 && deg < 22.5) || (deg > 337.5)) return "N";
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
