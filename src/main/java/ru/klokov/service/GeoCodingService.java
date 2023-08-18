package ru.klokov.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.klokov.dto.GeoCodingApiResponse;
import ru.klokov.util.UriUtil;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GeoCodingService {
    private final ObjectMapper mapper;

    public GeoCodingService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public List<GeoCodingApiResponse> getGeoDataByName(String name) throws JsonProcessingException, ExecutionException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(UriUtil.getGeoCodingUri(name)).build();

        CompletableFuture<String> responseFuture = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body);

        String responseJson = responseFuture.get();

        return mapper.readValue(responseJson, new TypeReference<>() {});
    }
}
