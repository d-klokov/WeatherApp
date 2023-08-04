package ru.klokov.dao;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.GenericJDBCException;
import ru.klokov.exception.DatabaseException;
import ru.klokov.exception.EntityAlreadyExistsException;
import ru.klokov.util.HibernateUtil;

import java.util.Optional;

public class SessionDAO {
    public void save(ru.klokov.model.Session session) {
        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = hibernateSession.beginTransaction();
            hibernateSession.persist(session);
            transaction.commit();
        } catch (ConstraintViolationException e) {
            throw new EntityAlreadyExistsException("Session with id: " + session.getId() + " already exists!");
        } catch (GenericJDBCException e) {
            throw new DatabaseException("Database error!");
        }
    }

    public Optional<ru.klokov.model.Session> findById(String id) {
        String hql = "From Session where id = :id";

        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            return hibernateSession.createQuery(hql, ru.klokov.model.Session.class).setParameter("id", id).uniqueResultOptional();
        } catch (HibernateException e) {
            throw new DatabaseException("Database error!\n" + e.getMessage());
        }
    }

    public void deleteById(String id) {
        String hql = "DELETE FROM Session WHERE id = :id";
        try (Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = hibernateSession.beginTransaction();
            hibernateSession.createMutationQuery(hql).setParameter("id", id).executeUpdate();
            transaction.commit();
        } catch (GenericJDBCException e) {
            throw new DatabaseException("Database error!");
        }
    }
}
