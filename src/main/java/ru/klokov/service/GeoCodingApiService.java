package ru.klokov.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.klokov.dto.GeoCodingApiResponse;
import ru.klokov.dto.GeoDataResponse;
import ru.klokov.util.OpenWeatherMapUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GeoCodingApiService {
    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public GeoCodingApiService(HttpClient httpClient, ObjectMapper mapper) {
        this.httpClient = httpClient;
        this.mapper = mapper;
    }

    public List<GeoCodingApiResponse> getGeoDataByLocationName(String name) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        HttpRequest request = HttpRequest.newBuilder().uri(getGeoCodingUri(name)).build();
        CompletableFuture<HttpResponse<String>> responseFuture = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .orTimeout(1, TimeUnit.MINUTES);
        HttpResponse<String> response = responseFuture.get();

        OpenWeatherMapUtil.checkStatusCode(response.statusCode());

        String responseJson = response.body();

        return mapper.readValue(responseJson, new TypeReference<List<GeoCodingApiResponse>>() {});
    }

    public GeoDataResponse getGeoDataResponse(GeoCodingApiResponse geoCodingApiResponse) {
        DecimalFormat format = new DecimalFormat("#.####");
        GeoDataResponse geoDataResponse = new GeoDataResponse();
        geoDataResponse.setName(geoCodingApiResponse.getName());
        geoDataResponse.setLatitude(format.format(geoCodingApiResponse.getLatitude()));
        geoDataResponse.setLongitude(format.format(geoCodingApiResponse.getLongitude()));
        geoDataResponse.setCountry(geoCodingApiResponse.getCountry());

        return geoDataResponse;
    }

    private URI getGeoCodingUri(String name) {
        String uri = OpenWeatherMapUtil.getBaseUrl() +
                OpenWeatherMapUtil.getGeocodingApiUrl() +
                "?q=" + name +
                "&limit=5" +
                "&appid=" + OpenWeatherMapUtil.getApiKey();

        return URI.create(uri);
    }
}
