package ru.klokov.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.klokov.dao.SessionDAO;
import ru.klokov.util.HibernateUtil;
import ru.klokov.dao.UserDAO;
import ru.klokov.service.AuthService;
import ru.klokov.util.OpenWeatherMapUtil;

@WebListener
public class ApplicationServletContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        HibernateUtil.getSessionFactory();

        UserDAO userDAO = new UserDAO();
        SessionDAO sessionDAO = new SessionDAO();
        AuthService authService = new AuthService(userDAO, sessionDAO);

        context.setAttribute("authService", authService);
    }
}
