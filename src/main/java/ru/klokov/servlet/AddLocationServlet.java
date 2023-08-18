package ru.klokov.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.klokov.dao.LocationDAO;
import ru.klokov.dao.UserDAO;
import ru.klokov.model.Location;
import ru.klokov.model.User;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/add-location")
public class AddLocationServlet extends BaseServlet {
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
        String locationName = req.getParameter("name");
        String latitudeParameter = req.getParameter("latitude");
        String longitudeParameter = req.getParameter("longitude");
        String login = req.getParameter("login");

        System.out.println(locationName + " " + latitudeParameter + " " +
                longitudeParameter + " " + login);

        // TODO validate params

        double latitude = Double.parseDouble(latitudeParameter);
        double longitude = Double.parseDouble(longitudeParameter);

        Location location = new Location(locationName, latitude, longitude);
        Optional<User> user = userDAO.findByLogin(login);
        user.ifPresent(location::setUser);

        System.out.println("Location of user: " + location.getUser().getLogin());

        locationDAO.save(location);

        resp.sendRedirect(req.getContextPath());
    }
}
