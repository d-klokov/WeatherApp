package ru.klokov.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.klokov.dao.LocationDAO;
import ru.klokov.dao.UserDAO;
import ru.klokov.exception.EntityNotFoundException;
import ru.klokov.model.User;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/delete-location")
public class DeleteLocationServlet extends BaseServlet {
    private LocationDAO locationDAO;
    private UserDAO userDAO;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        locationDAO = (LocationDAO) config.getServletContext().getAttribute("locationDAO");
        userDAO = (UserDAO) config.getServletContext().getAttribute("userDAO");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String locationIdParameter = req.getParameter("locationId");

        // TODO validate params
        Long locationId = Long.parseLong(locationIdParameter);

        // TODO remove location from User's locations list
        locationDAO.deleteById(locationId);

        resp.sendRedirect(req.getContextPath());
    }
}
