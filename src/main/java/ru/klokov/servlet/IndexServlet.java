package ru.klokov.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.klokov.dao.LocationDAO;
import ru.klokov.dto.weather.WeatherApiResponse;
import ru.klokov.dto.weather.WeatherResponse;
import ru.klokov.exception.InvalidParameterException;
import ru.klokov.exception.WeatherServiceException;
import ru.klokov.model.Location;
import ru.klokov.model.Session;
import ru.klokov.model.User;
import ru.klokov.service.AuthService;
import ru.klokov.service.WeatherApiService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@WebServlet("")
public class IndexServlet extends BaseServlet {
    private AuthService authService;
    private WeatherApiService weatherApiService;
    private LocationDAO locationDAO;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        authService = (AuthService) config.getServletContext().getAttribute("authService");
        weatherApiService = (WeatherApiService) config.getServletContext().getAttribute("weatherService");
        locationDAO = (LocationDAO) config.getServletContext().getAttribute("locationDAO");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Session session = authService.getAndValidateSession(req);
        User user = session.getUser();

        List<Location> locations = locationDAO.findByUser(user);

        List<WeatherResponse> weatherResponses = new ArrayList<>();

        if (!locations.isEmpty()) {
            weatherResponses = locations.stream().map(location -> {
                try {
                    WeatherApiResponse weatherApiResponse = weatherApiService.getWeatherDataByLocation(location);
                    WeatherResponse weatherResponse = weatherApiService.getWeatherResponse(weatherApiResponse);
                    weatherResponse.setLocationId(location.getId());
                    return weatherResponse;
                } catch (ExecutionException | InterruptedException | JsonProcessingException | TimeoutException e) {
                    throw new WeatherServiceException(e.getMessage());
                }
            }).collect(Collectors.toList());
        }

        webContext.setVariable("login", user.getLogin());
        webContext.setVariable("weatherResponses", weatherResponses);
        templateEngine.process("index", webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        authService.getAndValidateSession(req);

        String locationIdParameter = req.getParameter("locationId");

        if (locationIdParameter == null) throw new InvalidPropertiesFormatException("Location id required!");

        long locationId;
        try {
            locationId = Long.parseLong(locationIdParameter);
        } catch (NumberFormatException e) {
            throw new InvalidParameterException("Location id should be a number!");
        }

        locationDAO.deleteById(locationId);

        resp.sendRedirect(req.getContextPath());
    }
}
