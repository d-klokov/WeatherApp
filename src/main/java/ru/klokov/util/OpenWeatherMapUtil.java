package ru.klokov.util;

import lombok.Getter;
import ru.klokov.exception.*;
import ru.klokov.exception.openweathermap.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class OpenWeatherMapUtil {
    @Getter
    private static String baseUrl;
    @Getter
    private static String apiKey;
    @Getter
    private static String geocodingApiUrl;
    @Getter
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

    public static void checkStatusCode(int statusCode) {
        if (statusCode != 200) {
            switch (statusCode) {
                case 400:
                    throw new OpenWeatherMapApiBadRequestException("Nothing to geocode. https://openweathermap.org/faq for more info.");
                case 401:
                    throw new OpenWeatherMapApiInvalidApiKeyException("Invalid API key. Please see https://openweathermap.org/faq#error401 for more info.");
                case 404:
                    throw new OpenWeatherMapApiNotFoundException("Nothing found. Please see https://openweathermap.org/faq#error404 for more info.");
                case 429:
                    throw new OpenWeatherMapApiTooManyRequestsException("Too many requests. You have a Free plan of Professional subscriptions and make more than 60 API calls per minute. Please see https://openweathermap.org/faq#error429 for more info.");
                case 500:
                case 502:
                case 503:
                case 504:
                    throw new OpenWeatherMapApiInternalErrorException("Openweathermap API error. Please see https://openweathermap.org/faq#error500 for more info.");
            }
        }
    }
}
