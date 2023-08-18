package ru.klokov.listener;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.klokov.dao.LocationDAO;
import ru.klokov.dao.SessionDAO;
import ru.klokov.dto.GeoCodingApiResponse;
import ru.klokov.model.Location;
import ru.klokov.model.User;
import ru.klokov.service.GeoCodingService;
import ru.klokov.service.WeatherService;
import ru.klokov.util.HibernateUtil;
import ru.klokov.dao.UserDAO;
import ru.klokov.service.AuthService;

import java.util.List;
import java.util.concurrent.ExecutionException;

@WebListener
public class ApplicationServletContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        HibernateUtil.getSessionFactory();

        UserDAO userDAO = new UserDAO();
        SessionDAO sessionDAO = new SessionDAO();
        LocationDAO locationDAO = new LocationDAO();
        AuthService authService = new AuthService(userDAO, sessionDAO);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        GeoCodingService geoCodingService = new GeoCodingService(mapper);
        WeatherService weatherService = new WeatherService(mapper);

        User user = new User("hello@gmail.com", BCrypt.withDefaults().hashToString(12, "123".toCharArray()));
        userDAO.save(user);
        Location location = new Location("Azov", 39.426, 47.1139);
        location.setUser(user);
        locationDAO.save(location);


        context.setAttribute("authService", authService);
        context.setAttribute("mapper", mapper);
        context.setAttribute("geoCodingService", geoCodingService);
        context.setAttribute("weatherService", weatherService);
        context.setAttribute("locationDAO", locationDAO);
        context.setAttribute("userDAO", userDAO);
    }
}
