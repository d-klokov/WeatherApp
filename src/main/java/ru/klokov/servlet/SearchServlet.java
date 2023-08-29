package ru.klokov.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.klokov.dao.LocationDAO;
import ru.klokov.dao.UserDAO;
import ru.klokov.dto.GeoCodingApiResponse;
import ru.klokov.dto.GeoDataResponse;
import ru.klokov.exception.GeoCodingServiceException;
import ru.klokov.exception.InvalidParameterException;
import ru.klokov.model.Location;
import ru.klokov.model.Session;
import ru.klokov.model.User;
import ru.klokov.service.GeoCodingApiService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@WebServlet("/search")
public class SearchServlet extends BaseServlet {
    private GeoCodingApiService geoCodingApiService;
    private LocationDAO locationDAO;
    private UserDAO userDAO;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        geoCodingApiService = (GeoCodingApiService) config.getServletContext().getAttribute("geoCodingService");
        locationDAO = (LocationDAO) config.getServletContext().getAttribute("locationDAO");
        userDAO = (UserDAO) config.getServletContext().getAttribute("userDAO");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Session session = authService.getAndValidateSession(req);

        String locationName = req.getParameter("name");

        if (locationName == null || locationName.isBlank())
            throw new InvalidParameterException("Location name required!");

        List<GeoCodingApiResponse> geoCodingApiResponses;
        try {
            geoCodingApiResponses = geoCodingApiService.getGeoDataByLocationName(locationName);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new GeoCodingServiceException(e.getMessage());
        }

        List<GeoDataResponse> geoDataResponses = geoCodingApiResponses.stream()
                .map(response -> geoCodingApiService.getGeoDataResponse(response))
                .collect(Collectors.toList());

        webContext.setVariable("login", session.getUser().getLogin());
        webContext.setVariable("geoDataResponses", geoDataResponses);
        templateEngine.process("search-results", webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        authService.getAndValidateSession(req);

        String locationName = req.getParameter("name");
        String latitudeParameter = req.getParameter("latitude");
        String longitudeParameter = req.getParameter("longitude");
        String login = req.getParameter("login");

        if (locationName == null || locationName.isBlank()) throw new InvalidParameterException("Location name required");
        if (login == null || login.isBlank()) throw new InvalidParameterException("Login required");
        if (latitudeParameter == null || latitudeParameter.isBlank()) throw new InvalidParameterException("Latitude required");
        if (longitudeParameter == null || longitudeParameter.isBlank()) throw new InvalidParameterException("Longitude required");

        double latitude, longitude;
        try {
            latitude = Double.parseDouble(latitudeParameter);
            longitude = Double.parseDouble(longitudeParameter);
        } catch (NumberFormatException e) {
            throw new InvalidParameterException("Latitude and longitude must be a floating point numbers!");
        }

        Location location = new Location(locationName, latitude, longitude);
        Optional<User> user = userDAO.findByLogin(login);
        user.ifPresent(location::setUser);

        locationDAO.save(location);

        resp.sendRedirect(req.getContextPath());
    }
}
