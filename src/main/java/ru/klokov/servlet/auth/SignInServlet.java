package ru.klokov.servlet.auth;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.klokov.model.Session;
import ru.klokov.service.AuthService;
import ru.klokov.servlet.BaseServlet;
import ru.klokov.util.ValidatorUtil;

import java.io.IOException;

@WebServlet("/signin")
public class SignInServlet extends BaseServlet {
    private AuthService authService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        authService = (AuthService) config.getServletContext().getAttribute("authService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        templateEngine.process("signin", webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        ValidatorUtil.validateAuthParameters(email, password);

        Session session = authService.signIn(email, password);
        setSessionCookieAndRedirect(session, req, resp);
    }
}
