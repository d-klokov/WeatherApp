package ru.klokov.util;

import java.net.URI;

public class UriUtil {
    public static URI getGeoCodingUri(String name) {

        String uri = OpenWeatherMapUtil.getBaseUrl() +
                OpenWeatherMapUtil.getGeocodingApiUrl() +
                "?q=" + name +
                "&limit=5" +
                "&appid=" + OpenWeatherMapUtil.getApiKey();

        return URI.create(uri);
    }
}
