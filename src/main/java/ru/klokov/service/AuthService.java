package ru.klokov.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.thymeleaf.context.WebContext;
import ru.klokov.dao.SessionDAO;
import ru.klokov.dao.UserDAO;
import ru.klokov.exception.*;
import ru.klokov.model.Session;
import ru.klokov.model.User;
import ru.klokov.util.ValidatorUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class AuthService {
    private static final Long SESSION_EXPIRATION_TIME_IN_HOURS = 1L;
    private final UserDAO userDAO;
    private final SessionDAO sessionDAO;

    public AuthService(UserDAO userDAO, SessionDAO sessionDAO) {
        this.userDAO = userDAO;
        this.sessionDAO = sessionDAO;
    }

    public Session signUp(String email, String password) {
        String encodedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());

        User user = new User(email, encodedPassword);
        userDAO.save(user);

        return createSession(user);
    }

    public Session signIn(String email, String password) throws PasswordsNotMatchException {
        User user = userDAO.findByLogin(email)
                .orElseThrow(() -> new EntityNotFoundException("User with login " + email + " not found!"));

        if (ValidatorUtil.passwordsNotMatch(password, user.getPassword()))
            throw new PasswordsNotMatchException("Authentication failed: password is incorrect!");

        return createSession(user);
    }

    public void signOut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Cookie sessionCookie = getSessionCookie(req);
        sessionDAO.deleteById(sessionCookie.getValue());
        Cookie cookie = new Cookie("SESSIONID", null);
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
        resp.sendRedirect(req.getContextPath());
    }

    public Session getSessionById(String id) {
        return sessionDAO.findById(id).orElseThrow(SessionNotFoundException::new);
    }

    public Session createSession(User user) {
        Session session = new Session(UUID.randomUUID().toString(), user,
                LocalDateTime.now().plusHours(SESSION_EXPIRATION_TIME_IN_HOURS));
        sessionDAO.save(session);
        return session;
    }

    public Cookie getSessionCookie(HttpServletRequest req) {
        if (req.getCookies() == null) throw new CookiesNotFoundException();

        return Arrays.stream(req.getCookies()).filter(cookie -> cookie.getName().equals("SESSIONID")).findFirst()
                .orElseThrow(CookiesNotFoundException::new);
    }

    public boolean sessionExpired(Session session) {
        return LocalDateTime.now().isAfter(session.getExpiresAt());
    }

//    public User getAuthenticatedUser(HttpServletRequest req) {
//        Cookie sessionCookie = getSessionCookie(req);
//        Session session = getSessionById(sessionCookie.getValue());
//        if (sessionExpired(session)) throw new SessionExpiredException();
//
//        return session.getUser();
//    }

    public Session getAndValidateSession(HttpServletRequest req) {
        Cookie sessionCookie = getSessionCookie(req);
        Session session = getSessionById(sessionCookie.getValue());
        if (sessionExpired(session)) throw new SessionExpiredException();

        return session;
    }
}
