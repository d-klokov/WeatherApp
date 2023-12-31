package ru.klokov.dao;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.GenericJDBCException;
import ru.klokov.exception.DatabaseException;
import ru.klokov.exception.EntityAlreadyExistsException;
import ru.klokov.model.Location;
import ru.klokov.model.User;
import ru.klokov.util.HibernateUtil;

import java.util.List;

public class LocationDAO {
    public void save(Location location) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(location);
            transaction.commit();
        } catch (ConstraintViolationException e) {
            throw new EntityAlreadyExistsException("Location with geographical coordinates: " +
                    location.getLatitude() + " " + location.getLongitude() + " already exists!");
        } catch (GenericJDBCException e) {
            throw new DatabaseException("Database error!");
        }
    }

    public Location findById(Long id) {
        String hql = "From Location where id = :id";

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(hql, Location.class).setParameter("id", id).getSingleResult();
        } catch (HibernateException e) {
            throw new DatabaseException("Database error!\n" + e.getMessage());
        }
    }

    public List<Location> findByUser(User user) {
        String hql = "From Location where user.id = :user_id";

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(hql, Location.class).setParameter("user_id", user.getId()).list();
        } catch (HibernateException e) {
            throw new DatabaseException("Database error!\n" + e.getMessage());
        }
    }

    public void deleteById(Long id) {
        String hql = "Delete from Location where id = :id";

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createMutationQuery(hql).setParameter("id", id).executeUpdate();
            transaction.commit();
        } catch (HibernateException e) {
            throw new DatabaseException("Database error!\n" + e.getMessage());
        }
    }
}
