package ru.klokov.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.klokov.dao.LocationDAO;
import ru.klokov.dto.GeoCodingApiResponse;
import ru.klokov.dto.GeoDataResponse;
import ru.klokov.exception.GeoCodingServiceException;
import ru.klokov.exception.InvalidParameterException;
import ru.klokov.model.Location;
import ru.klokov.model.Session;
import ru.klokov.service.GeoCodingApiService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@WebServlet("/search")
public class SearchServlet extends BaseServlet {
    private GeoCodingApiService geoCodingApiService;
    private LocationDAO locationDAO;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        geoCodingApiService = (GeoCodingApiService) config.getServletContext().getAttribute("geoCodingService");
        locationDAO = (LocationDAO) config.getServletContext().getAttribute("locationDAO");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Session session = authService.getAndValidateSession(req);

        String locationName = URLEncoder.encode(req.getParameter("name"), StandardCharsets.UTF_8);

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
        webContext.setVariable("locationName", locationName);
        webContext.setVariable("geoDataResponses", geoDataResponses);
        templateEngine.process("search-results", webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Session session = authService.getAndValidateSession(req);

        String locationName = req.getParameter("name");
        String latitudeParameter = req.getParameter("latitude");
        String longitudeParameter = req.getParameter("longitude");
//        String login = req.getParameter("login");

        if (locationName == null || locationName.isBlank()) throw new InvalidParameterException("Location name required");
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
        location.setUser(session.getUser());

        locationDAO.save(location);

        resp.sendRedirect(req.getContextPath());
    }
}
