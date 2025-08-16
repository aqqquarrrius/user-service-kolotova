package com.example.userservice;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

public class HibernateTestUtil {

    private static SessionFactory sessionFactory;

    public static void configure(String jdbcUrl, String username, String password) {
        if (sessionFactory == null) {
            Configuration configuration = new Configuration();
            configuration.addAnnotatedClass(User.class);

            Properties props = new Properties();
            props.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
            props.setProperty("hibernate.connection.url", jdbcUrl);
            props.setProperty("hibernate.connection.username", username);
            props.setProperty("hibernate.connection.password", password);
            props.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            props.setProperty("hibernate.hbm2ddl.auto", "update");
            props.setProperty("hibernate.show_sql", "true");

            configuration.setProperties(props);
            sessionFactory = configuration.buildSessionFactory();
        }
    }
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            throw new IllegalStateException("SessionFactory not configured. Call configure() first.");
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }
}
