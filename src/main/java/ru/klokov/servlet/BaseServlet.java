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

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class BaseServlet extends HttpServlet {
    protected ITemplateEngine templateEngine;
    protected WebContext webContext;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        templateEngine = (TemplateEngine) config.getServletContext().getAttribute("templateEngine");
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        IWebExchange webExchange = JakartaServletWebApplication.buildApplication(getServletContext())
                .buildExchange(req, resp);
        webContext = new WebContext(webExchange);

        try {
            super.service(req, resp);
        } catch (DatabaseException e) {
            System.out.println("500 " + e.getMessage());
        } catch (EntityAlreadyExistsException | EntityNotFoundException | PasswordsNotMatchException e) {
            webContext.setVariable("error", e.getMessage());
            templateEngine.process("signin", webContext, resp.getWriter());
        } catch (SessionNotFoundException | CookiesNotFoundException | SessionExpiredException e) {
            resp.sendRedirect(req.getContextPath() + "/signin");
        }
    }

    public void setSessionCookieAndRedirect(Session session, HttpServletRequest req, HttpServletResponse resp, WebContext webContext) throws IOException {
        resp.addCookie(new Cookie("SESSIONID", session.getId()));
        webContext.setVariable("authenticated", true);
        resp.sendRedirect(req.getContextPath());
    }
}
