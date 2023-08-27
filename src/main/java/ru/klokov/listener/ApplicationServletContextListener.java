package ru.klokov.listener;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.klokov.dao.LocationDAO;
import ru.klokov.dao.SessionDAO;
import ru.klokov.service.GeoCodingApiService;
import ru.klokov.service.WeatherApiService;
import ru.klokov.util.HibernateUtil;
import ru.klokov.dao.UserDAO;
import ru.klokov.service.AuthService;

import java.net.http.HttpClient;

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

        HttpClient httpClient = HttpClient.newHttpClient();
        GeoCodingApiService geoCodingApiService = new GeoCodingApiService(httpClient, mapper);
        WeatherApiService weatherApiService = new WeatherApiService(httpClient, mapper);

//        User user = new User("hello@gmail.com", BCrypt.withDefaults().hashToString(12, "123".toCharArray()));
//        userDAO.save(user);
//
//        sessionDAO.save(new Session(
//                UUID.randomUUID().toString(),
//                user,
//                LocalDateTime.now().plusHours(1)
//        ));
//
//        for (int i = 0; i < 3; i++) {
//            sessionDAO.save(new Session(
//                    UUID.randomUUID().toString(),
//                    user,
//                    LocalDateTime.now().minusHours(i + 1)
//            ));
//        }

        context.setAttribute("authService", authService);
        context.setAttribute("mapper", mapper);
        context.setAttribute("geoCodingService", geoCodingApiService);
        context.setAttribute("weatherService", weatherApiService);
        context.setAttribute("locationDAO", locationDAO);
        context.setAttribute("userDAO", userDAO);

        authService.deleteExpiredSessions();
    }
}
