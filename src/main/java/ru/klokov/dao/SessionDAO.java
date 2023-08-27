package ru.klokov.dao;

import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.GenericJDBCException;
import ru.klokov.exception.DatabaseException;
import ru.klokov.exception.EntityAlreadyExistsException;
import ru.klokov.model.Session;
import ru.klokov.util.HibernateUtil;

import java.time.LocalDateTime;
import java.util.Optional;

public class SessionDAO {
    public void save(Session session) {
        try (org.hibernate.Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = hibernateSession.beginTransaction();
            hibernateSession.persist(session);
            transaction.commit();
        } catch (ConstraintViolationException e) {
            throw new EntityAlreadyExistsException("Session with id: " + session.getId() + " already exists!");
        } catch (GenericJDBCException e) {
            throw new DatabaseException("Database error!");
        }
    }

    public Optional<Session> findById(String id) {
        String hql = "From Session where id = :id";

        try (org.hibernate.Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            return hibernateSession.createQuery(hql, Session.class).setParameter("id", id).uniqueResultOptional();
        } catch (HibernateException e) {
            throw new DatabaseException("Database error!\n" + e.getMessage());
        }
    }

    public void deleteById(String id) {
        String hql = "DELETE FROM Session WHERE id = :id";
        try (org.hibernate.Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = hibernateSession.beginTransaction();
            hibernateSession.createMutationQuery(hql).setParameter("id", id).executeUpdate();
            transaction.commit();
        } catch (HibernateException e) {
            throw new DatabaseException("Database error!");
        }
    }

    public void deleteExpiredSessions() {
        String sql = "DELETE FROM sessions WHERE expires_at <= :dateTime";
//        String hql = "DELETE FROM Session WHERE expires_at <= :(dateTime)";
        Transaction transaction = null;
        try (org.hibernate.Session hibernateSession = HibernateUtil.getSessionFactory().openSession()) {
            transaction = hibernateSession.beginTransaction();
            hibernateSession.createNativeMutationQuery(sql)
                    .setParameter("dateTime", LocalDateTime.now()).executeUpdate();
//            int x = hibernateSession.createQuery(hql, Integer.class)
//                    .setParameter("dateTime", LocalDateTime.now()).executeUpdate();
//            System.out.println(x);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            throw new DatabaseException("Database error!");
        }
    }
}
