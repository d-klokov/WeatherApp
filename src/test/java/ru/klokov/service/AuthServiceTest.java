package ru.klokov.service;

import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.klokov.dao.SessionDAO;
import ru.klokov.dao.UserDAO;
import ru.klokov.exception.DatabaseException;
import ru.klokov.exception.EntityAlreadyExistsException;
import ru.klokov.exception.EntityNotFoundException;
import ru.klokov.exception.PasswordsNotMatchException;
import ru.klokov.model.Session;
import ru.klokov.model.User;
import ru.klokov.util.HibernateUtil;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {
    private final UserDAO userDAO;
    private final SessionDAO sessionDAO;
    private final AuthService authService;

    public AuthServiceTest() {
        userDAO = new UserDAO();
        sessionDAO = new SessionDAO();
        authService = new AuthService(userDAO, sessionDAO);
    }

    @BeforeEach
    public void clearTables() {
        String clearUsersHQL = "Delete from User";
        String clearSessionsHQL = "Delete from Session";

        try (org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createMutationQuery(clearUsersHQL).executeUpdate();
            session.createMutationQuery(clearSessionsHQL).executeUpdate();
            transaction.commit();
        } catch (HibernateException e) {
            throw new DatabaseException("Database error!\n" + e.getMessage());
        }
    }

    @Test
    @DisplayName("When sign up successfully should create new user and session")
    public void signUpSuccess() {
        String login = "hello@gmail.com";
        String password = "password";

        Session createdSession = authService.signUp(login, password);
        Optional<User> user = userDAO.findByLogin(login);
        Optional<Session> session = sessionDAO.findById(createdSession.getId());

        assertTrue(user.isPresent());
        assertTrue(session.isPresent());
        assertEquals(user.get().getLogin(), login);
        assertEquals(session.get().getId(), createdSession.getId());
    }

    @Test
    @DisplayName("When sign up with existed login should throw EntityAlreadyExistsException")
    public void signUpEntityAlreadyExistsException() {
        String login = "hello@gmail.com";
        String password = "password";

        authService.signUp(login, password);

        assertThrows(EntityAlreadyExistsException.class, () -> authService.signUp(login, password));
    }

    @Test
    @DisplayName("When sign in successfully should create new session")
    public void signInSuccess() {
        String login = "hello@gmail.com";
        String password = "password";

        authService.signUp(login, password);
        Session createdSession = authService.signIn(login, password);
        Optional<Session> session = sessionDAO.findById(createdSession.getId());

        assertTrue(session.isPresent());
        assertEquals(session.get().getId(), createdSession.getId());
    }

    @Test
    @DisplayName("When sign in with not existed login should throw EntityNotFoundException")
    public void signInEntityNotFoundException() {
        String login = "hello@gmail.com";
        String password = "password";

        assertThrows(EntityNotFoundException.class, () -> authService.signIn(login, password));
    }

    @Test
    @DisplayName("When sign in with incorrect password should throw PasswordsNotMatchException")
    public void signInPasswordsNotMatchException() {
        String login = "hello@gmail.com";
        String password = "password";
        String wrongPassword = "wrong_password";

        authService.signUp(login, password);

        assertThrows(PasswordsNotMatchException.class, () -> authService.signIn(login, wrongPassword));
    }

    @Test
    @DisplayName("When session expires should throw SessionExpiredException")
    public void sessionExpiredException() {
        String login = "hello@gmail.com";
        String password = "password";

        Session session = new Session(
                UUID.randomUUID().toString(),
                new User(login, password),
                LocalDateTime.MIN
        );

        assertTrue(authService.sessionExpired(session));
    }
}
