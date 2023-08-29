package ru.klokov.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.klokov.dto.weather.WeatherApiResponse;
import ru.klokov.exception.openweathermap.*;
import ru.klokov.model.Location;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WeatherApiServiceTest {
    @Mock
    private Location location;
    @Mock
    private HttpResponse<String> response;
    @Mock
    private HttpClient httpClient;
    @Spy
    private final ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private WeatherApiService weatherApiService;

    @Test
    @DisplayName("When get weather by coordinates successfully should return weather response json")
    public void getWeatherByLocationNameSuccess() throws ExecutionException, InterruptedException, JsonProcessingException, TimeoutException {
        String json = "{\"coord\":{\"lon\":37.6175,\"lat\":55.7504}," +
                "\"weather\":[{\"id\":804,\"main\":\"Clouds\",\"description\":\"overcast clouds\",\"icon\":\"04d\"}]," +
                "\"base\":\"stations\",\"main\":{\"temp\":18.04,\"feels_like\":17.62,\"temp_min\":16.15," +
                "\"temp_max\":18.43,\"pressure\":1009,\"humidity\":66,\"sea_level\":1009,\"grnd_level\":992},\"visibility\":10000," +
                "\"wind\":{\"speed\":4.07,\"deg\":297,\"gust\":5.63},\"clouds\":{\"all\":97},\"dt\":1692798493,\"sys\":{\"type\":2,\"id\":2000314," +
                "\"country\":\"RU\",\"sunrise\":1692756965,\"sunset\":1692809335},\"timezone\":10800,\"id\":524901,\"name\":\"Moscow\",\"cod\":200}";
        String expectedLocationName = "Moscow";
        double expectedTemperature = 18.04;

        when(httpClient.sendAsync(any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(CompletableFuture.completedFuture(response));
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn(json);

        WeatherApiResponse weatherApiResponse = weatherApiService.getWeatherDataByLocation(location);

        assertEquals(weatherApiResponse.getLocationName(), expectedLocationName);
        assertEquals(weatherApiResponse.getMain().getTemperature(), expectedTemperature);
    }

    @Test
    @DisplayName("When trying to get weather with invalid search parameters, should throw OpenWeatherMapApiBadRequestException")
    public void getWeatherByLocationNameOpenWeatherMapApiBadRequestException() {
        String expectedMessage = "Nothing to geocode.";

        when(httpClient.sendAsync(any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(CompletableFuture.completedFuture(response));
        when(response.statusCode()).thenReturn(400);

        OpenWeatherMapApiBadRequestException exception = assertThrows(OpenWeatherMapApiBadRequestException.class,
                () -> weatherApiService.getWeatherDataByLocation(location));
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    @DisplayName("When trying to get weather with invalid API key, should throw OpenWeatherMapApiInvalidApiKeyException")
    public void getGeoDataByLocationNameOpenWeatherMapApiInvalidApiKeyException() {
        String expectedMessage = "Invalid API key.";

        when(httpClient.sendAsync(any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(CompletableFuture.completedFuture(response));
        when(response.statusCode()).thenReturn(401);

        OpenWeatherMapApiInvalidApiKeyException exception = assertThrows(OpenWeatherMapApiInvalidApiKeyException.class,
                () -> weatherApiService.getWeatherDataByLocation(location));
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    @DisplayName("When trying to get weather with invalid location name, throw OpenWeatherMapApiNotFoundException")
    public void getGeoDataByLocationNameOpenWeatherMapApiNotFoundException() {
        String expectedMessage = "Nothing found.";

        when(httpClient.sendAsync(any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(CompletableFuture.completedFuture(response));
        when(response.statusCode()).thenReturn(404);

        OpenWeatherMapApiNotFoundException exception = assertThrows(OpenWeatherMapApiNotFoundException.class,
                () -> weatherApiService.getWeatherDataByLocation(location));
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    @DisplayName("When trying to get weather more than 60 times per minute, should throw OpenWeatherMapApiTooManyRequestsException")
    public void getGeoDataByLocationNameOpenWeatherMapApiTooManyRequestsException() {
        String expectedMessage = "Too many requests.";

        when(httpClient.sendAsync(any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(CompletableFuture.completedFuture(response));
        when(response.statusCode()).thenReturn(429);

        OpenWeatherMapApiTooManyRequestsException exception = assertThrows(OpenWeatherMapApiTooManyRequestsException.class,
                () -> weatherApiService.getWeatherDataByLocation(location));
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    @DisplayName("When trying to get weather and Openweathermap not respond, throw OpenWeatherMapApiInternalErrorException")
    public void getGeoDataByLocationNameOpenWeatherMapApiInternalErrorException() {
        String expectedMessage = "Openweathermap API error.";

        when(httpClient.sendAsync(any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(CompletableFuture.completedFuture(response));
        when(response.statusCode()).thenReturn(500);

        OpenWeatherMapApiInternalErrorException exception = assertThrows(OpenWeatherMapApiInternalErrorException.class,
                () -> weatherApiService.getWeatherDataByLocation(location));
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
}
