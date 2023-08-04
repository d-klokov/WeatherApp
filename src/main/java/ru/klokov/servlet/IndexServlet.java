package ru.klokov.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.klokov.exception.SessionExpiredException;
import ru.klokov.model.Session;
import ru.klokov.service.AuthService;

import java.io.IOException;

@WebServlet("")
public class IndexServlet extends BaseServlet {
    private AuthService authService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        authService = (AuthService) config.getServletContext().getAttribute("authService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        Optional<Cookie> cookieOpt = authService.getSessionCookie(req);
//        if (cookieOpt.isPresent()) {
//            Session session = authService.getSessionById(cookieOpt.get().getValue());
//            if (authService.sessionNotExpired(session)) {
//                webContext.setVariable("authenticated", true);
//                webContext.setVariable("user", session.getUser());
//                templateEngine.process("index", webContext, resp.getWriter());
//            } else resp.sendRedirect(req.getContextPath() + "/signin");
//        } else resp.sendRedirect(req.getContextPath() + "/signin");
        Cookie sessionCookie = authService.getSessionCookie(req);
        Session session = authService.getSessionById(sessionCookie.getValue());

        if (authService.sessionExpired(session)) throw new SessionExpiredException();

        webContext.setVariable("authenticated", true);
        webContext.setVariable("user", session.getUser());
        templateEngine.process("index", webContext, resp.getWriter());
    }
}
