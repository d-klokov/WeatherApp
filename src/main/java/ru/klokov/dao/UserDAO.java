package ru.klokov.dao;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.GenericJDBCException;
import ru.klokov.exception.DatabaseException;
import ru.klokov.exception.LocationAlreadyExistsException;
import ru.klokov.model.User;
import ru.klokov.util.HibernateUtil;

import java.util.Optional;

public class UserDAO {
    public void save(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
        } catch (ConstraintViolationException e) {
            throw new LocationAlreadyExistsException("User with login: " + user.getLogin() + " already exists!");
        } catch (GenericJDBCException e) {
            throw new DatabaseException("Database error!");
        }
    }

    public Optional<User> findByLogin(String login) {
        String hql = "From User where login = :login";

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(hql, User.class).setParameter("login", login).uniqueResultOptional();
        } catch (HibernateException e) {
            throw new DatabaseException("Database error!\n" + e.getMessage());
        }
    }
}
