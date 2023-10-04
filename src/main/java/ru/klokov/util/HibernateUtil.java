package ru.klokov.util;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import ru.klokov.exception.DatabaseException;
import ru.klokov.exception.LoadingPropertiesFailedException;
import ru.klokov.model.Location;
import ru.klokov.model.Session;
import ru.klokov.model.User;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private static String host;
    private static String database;
    private static String username;
    private static String password;

    static {
        readApplicationProperties();
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = getConfiguration();

                configuration.addAnnotatedClass(User.class);
                configuration.addAnnotatedClass(Location.class);
                configuration.addAnnotatedClass(Session.class);

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (HibernateException e) {
                throw new DatabaseException(e.getMessage());
            }
        }

        return sessionFactory;
    }

    private static void readApplicationProperties() {
        Properties applicationProperties = new Properties();

        try (InputStream input = HibernateUtil.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) throw new LoadingPropertiesFailedException("Can't fina application properties file!");

            applicationProperties.load(input);

            host = applicationProperties.getProperty("HOST");
            database = applicationProperties.getProperty("POSTGRES_DATABASE");
            username = applicationProperties.getProperty("POSTGRES_USERNAME");
            password = applicationProperties.getProperty("POSTGRES_PASSWORD");
        } catch (IOException e) {
            throw new LoadingPropertiesFailedException(e.getMessage());
        }
    }

    private static Configuration getConfiguration() {
        Configuration configuration = new Configuration();

        Properties properties = new Properties();

        properties.put(Environment.DRIVER, "org.postgresql.Driver");
        properties.put(Environment.URL, "jdbc:postgresql://" + host + ":5432/" + database);
        properties.put(Environment.USER, username);
        properties.put(Environment.PASS, password);
        properties.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
        properties.put(Environment.HBM2DDL_AUTO, "create-drop");
        properties.put(Environment.SHOW_SQL, "true");
        properties.put(Environment.FORMAT_SQL, "true");
//        properties.put(Environment.CONNECTION_PROVIDER, "com.zaxxer.hikari.hibernate.HikariConnectionProvider");

        configuration.setProperties(properties);
        return configuration;
    }
}
