package ru.klokov.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class OpenWeatherMapUtil {
    private static String baseUrl;
    private static String apiKey;

    static {
        loadProperties();
    }

    public static void loadProperties() {
        try (InputStream input = OpenWeatherMapUtil.class.getClassLoader().getResourceAsStream("openweathermap.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            baseUrl = prop.getProperty("BASE_URL");
            apiKey = prop.getProperty("API_KEY");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static String getApiKey() {
        return apiKey;
    }
}
