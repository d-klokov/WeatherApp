package ru.klokov.util;

import ru.klokov.exception.LoadingPropertiesFailedException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class OpenWeatherMapUtil {
    private static String baseUrl;
    private static String apiKey;
    private static String geocodingApiUrl;
    private static String currentWeatherApiUrl;

    static {
        loadProperties();
    }

    public static void loadProperties() {
        try (InputStream input = OpenWeatherMapUtil.class.getClassLoader().getResourceAsStream("openweathermap.properties")) {
            Properties properties = new Properties();
            properties.load(input);

            baseUrl = properties.getProperty("BASE_URL");
            geocodingApiUrl = properties.getProperty("GEOCODING_API_URL");
            currentWeatherApiUrl = properties.getProperty("CURRENT_WEATHER_API_URL");
            apiKey = properties.getProperty("API_KEY");
        } catch (IOException ex) {
            throw new LoadingPropertiesFailedException();
        }
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static String getGeocodingApiUrl() {
        return geocodingApiUrl;
    }

    public static String getCurrentWeatherApiUrl() {
        return currentWeatherApiUrl;
    }

    public static String getApiKey() {
        return apiKey;
    }
}
