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
import ru.klokov.dto.GeoCodingApiResponse;
import ru.klokov.exception.openweathermap.*;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GeoCodingApiServiceTest {
    @Mock
    private HttpResponse<String> response;
    @Mock
    private HttpClient httpClient;
    @Spy
    private final ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private GeoCodingApiService geoCodingApiService;

    @Test
    @DisplayName("When get geo data by location name successfully should return json response with list of locations")
    public void getGeoDataByLocationNameSuccess() throws ExecutionException, InterruptedException, JsonProcessingException {
        String json = "[{\"name\":\"Moscow\",\"lat\":55.7504461,\"lon\":37.6174943,\"country\":\"RU\",\"state\":\"Moscow\"}," +
                "{\"name\":\"Moscow\",\"lat\":46.7323875,\"lon\":-117.0001651,\"country\":\"US\",\"state\":\"Idaho\"}," +
                "{\"name\":\"Moscow\",\"lat\":45.071096,\"lon\":-69.891586,\"country\":\"US\",\"state\":\"Maine\"}," +
                "{\"name\":\"Moscow\",\"lat\":35.0619984,\"lon\":-89.4039612,\"country\":\"US\",\"state\":\"Tennessee\"}," +
                "{\"name\":\"Moscow\",\"lat\":39.5437014,\"lon\":-79.0050273,\"country\":\"US\",\"state\":\"Maryland\"}]";
        String expectedLocationName = "Moscow";

        when(httpClient.sendAsync(any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(CompletableFuture.completedFuture(response));
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn(json);

        List<GeoCodingApiResponse> geoCodingApiResponses = geoCodingApiService.getGeoDataByLocationName(expectedLocationName);

        for (GeoCodingApiResponse resp : geoCodingApiResponses) {
            assertEquals(expectedLocationName, resp.getName());
        }
    }

    @Test
    @DisplayName("When trying to get geo data with invalid search parameters, should throw OpenWeatherMapApiBadRequestException")
    public void getGeoDataByLocationNameOpenWeatherMapApiBadRequestException() {
        String expectedMessage = "Nothing to geocode.";

        when(httpClient.sendAsync(any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(CompletableFuture.completedFuture(response));
        when(response.statusCode()).thenReturn(400);

        OpenWeatherMapApiBadRequestException exception = assertThrows(OpenWeatherMapApiBadRequestException.class,
                () -> geoCodingApiService.getGeoDataByLocationName(""));
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    @DisplayName("When trying to get geo data with invalid API key, should throw OpenWeatherMapApiInvalidApiKeyException")
    public void getGeoDataByLocationNameOpenWeatherMapApiInvalidApiKeyException() {
        String expectedMessage = "Invalid API key.";

        when(httpClient.sendAsync(any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(CompletableFuture.completedFuture(response));
        when(response.statusCode()).thenReturn(401);

        OpenWeatherMapApiInvalidApiKeyException exception = assertThrows(OpenWeatherMapApiInvalidApiKeyException.class,
                () -> geoCodingApiService.getGeoDataByLocationName("Moscow"));
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    @DisplayName("When trying to get geo data with invalid location name, throw OpenWeatherMapApiNotFoundException")
    public void getGeoDataByLocationNameOpenWeatherMapApiNotFoundException() {
        String expectedMessage = "Nothing found.";

        when(httpClient.sendAsync(any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(CompletableFuture.completedFuture(response));
        when(response.statusCode()).thenReturn(404);

        OpenWeatherMapApiNotFoundException exception = assertThrows(OpenWeatherMapApiNotFoundException.class,
                () -> geoCodingApiService.getGeoDataByLocationName("Moscow"));
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    @DisplayName("When trying to get geo data more than 60 times per minute, should throw OpenWeatherMapApiTooManyRequestsException")
    public void getGeoDataByLocationNameOpenWeatherMapApiTooManyRequestsException() {
        String expectedMessage = "Too many requests.";

        when(httpClient.sendAsync(any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(CompletableFuture.completedFuture(response));
        when(response.statusCode()).thenReturn(429);

        OpenWeatherMapApiTooManyRequestsException exception = assertThrows(OpenWeatherMapApiTooManyRequestsException.class,
                () -> geoCodingApiService.getGeoDataByLocationName("Moscow"));
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    @DisplayName("When trying to get geo data and Openweathermap not respond, throw OpenWeatherMapApiInternalErrorException")
    public void getGeoDataByLocationNameOpenWeatherMapApiInternalErrorException() {
        String expectedMessage = "Openweathermap API error.";

        when(httpClient.sendAsync(any(), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(CompletableFuture.completedFuture(response));
        when(response.statusCode()).thenReturn(500);

        OpenWeatherMapApiInternalErrorException exception = assertThrows(OpenWeatherMapApiInternalErrorException.class,
                () -> geoCodingApiService.getGeoDataByLocationName("Moscow"));
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
}
