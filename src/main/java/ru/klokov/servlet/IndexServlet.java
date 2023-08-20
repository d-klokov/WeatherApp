package ru.klokov.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.klokov.dao.LocationDAO;
import ru.klokov.dto.weather.WeatherApiResponse;
import ru.klokov.dto.weather.WeatherResponse;
import ru.klokov.exception.SessionExpiredException;
import ru.klokov.exception.WeatherApiException;
import ru.klokov.model.Location;
import ru.klokov.model.Session;
import ru.klokov.model.User;
import ru.klokov.service.AuthService;
import ru.klokov.service.WeatherService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@WebServlet("")
public class IndexServlet extends BaseServlet {
    private AuthService authService;
    private WeatherService weatherService;
    private LocationDAO locationDAO;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        authService = (AuthService) config.getServletContext().getAttribute("authService");
        weatherService = (WeatherService) config.getServletContext().getAttribute("weatherService");
        locationDAO = (LocationDAO) config.getServletContext().getAttribute("locationDAO");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Session session = authService.getAndValidateSession(req);
        User user = session.getUser();

        List<Location> locations = locationDAO.findByUser(user);

        List<WeatherResponse> weatherResponses = new ArrayList<>();

        if (!locations.isEmpty()) {
            weatherResponses = locations.stream().map(location -> {
                try {
                    WeatherApiResponse weatherApiResponse = weatherService.getWeatherDataByLocation(location);
                    WeatherResponse weatherResponse = weatherService.getWeatherResponse(weatherApiResponse);
                    weatherResponse.setLocationId(location.getId());
                    System.out.println(weatherResponse);
                    return weatherResponse;
                } catch (ExecutionException | InterruptedException | JsonProcessingException e) {
                    throw new WeatherApiException(e.getMessage());
                }
            }).collect(Collectors.toList());
        }


        webContext.setVariable("login", user.getLogin());
        webContext.setVariable("weatherResponses", weatherResponses);
        templateEngine.process("index", webContext, resp.getWriter());
    }
}
