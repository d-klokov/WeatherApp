package ru.klokov.servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import ru.klokov.exception.*;
import ru.klokov.model.Session;
import ru.klokov.service.AuthService;

import java.io.IOException;

public class BaseServlet extends HttpServlet {
    private AuthService authService;
    protected ITemplateEngine templateEngine;
    protected WebContext webContext;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        authService = (AuthService) config.getServletContext().getAttribute("authService");
        templateEngine = (TemplateEngine) config.getServletContext().getAttribute("templateEngine");
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        IWebExchange webExchange = JakartaServletWebApplication.buildApplication(getServletContext())
                .buildExchange(req, resp);
        webContext = new WebContext(webExchange);

        try {
            super.service(req, resp);
        } catch (SessionNotFoundException | CookiesNotFoundException | SessionExpiredException |
                 EntityNotFoundException | PasswordsNotMatchException e) {
            webContext.setVariable("error", e.getMessage());
            templateEngine.process("signin", webContext, resp.getWriter());
        } catch (LocationAlreadyExistsException | DatabaseException | InvalidParameterException e) {
            webContext.setVariable("message", e.getMessage());
            templateEngine.process("error", webContext, resp.getWriter());
        }
    }

    public void setSessionCookieAndRedirect(Session session, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.addCookie(new Cookie("SESSIONID", session.getId()));
        resp.sendRedirect(req.getContextPath());
    }
}
