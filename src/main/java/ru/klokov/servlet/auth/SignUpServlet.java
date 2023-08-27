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

@WebServlet("/signup")
public class SignUpServlet extends BaseServlet {
    private AuthService authService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        authService = (AuthService) config.getServletContext().getAttribute("authService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        templateEngine.process("signup", webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        ValidatorUtil.validateAuthParameters(email, password);

        Session session = authService.signUp(email, password);
        setSessionCookieAndRedirect(session, req, resp);
    }
}
