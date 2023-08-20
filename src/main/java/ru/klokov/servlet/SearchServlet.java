package ru.klokov.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.klokov.dto.GeoCodingApiResponse;
import ru.klokov.exception.GeoCodingApiException;
import ru.klokov.exception.InvalidParameterException;
import ru.klokov.exception.SessionExpiredException;
import ru.klokov.model.Session;
import ru.klokov.service.AuthService;
import ru.klokov.service.GeoCodingService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@WebServlet("/search")
public class SearchServlet extends BaseServlet {
    private AuthService authService;
    private GeoCodingService geoCodingService;
    private ObjectMapper mapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        authService = (AuthService) config.getServletContext().getAttribute("authService");
        geoCodingService = (GeoCodingService) config.getServletContext().getAttribute("geoCodingService");
        mapper = (ObjectMapper) config.getServletContext().getAttribute("mapper");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String locationName = req.getParameter("name");

        // TODO validate name parameter
        if (locationName == null || locationName.isBlank())
            throw new InvalidParameterException("Location name required!");

        Cookie sessionCookie = authService.getSessionCookie(req);
        Session session = authService.getSessionById(sessionCookie.getValue());

        if (authService.sessionExpired(session)) throw new SessionExpiredException();

        List<GeoCodingApiResponse> geoCodingResponses;
        try {
            geoCodingResponses = geoCodingService.getGeoDataByName(locationName);
        } catch (InterruptedException | ExecutionException e) {
            throw new GeoCodingApiException(e.getMessage());
        }

        webContext.setVariable("login", session.getUser().getLogin());
        webContext.setVariable("geoCodingResponses", geoCodingResponses);
        templateEngine.process("search-results", webContext, resp.getWriter());
    }
}
