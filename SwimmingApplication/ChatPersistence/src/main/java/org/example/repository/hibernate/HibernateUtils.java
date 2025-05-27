package org.example.repository.hibernate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.hibernate.ParticipantHibernate;
import org.example.model.hibernate.ParticipantRaceHibernate;
import org.example.model.hibernate.RaceHibernate;
import org.example.model.hibernate.UserHibernate;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;


public class HibernateUtils {
    private static final Logger logger = LogManager.getLogger(HibernateUtils.class);
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory(Properties props) {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            logger.debug("Creating new Hibernate SessionFactory");
            sessionFactory = createSessionFactory(props);
        } else {
            logger.trace("Reusing existing Hibernate SessionFactory");
        }
        return sessionFactory;
    }

    private static SessionFactory createSessionFactory(Properties props) {
        logger.info("Initializing Hibernate SessionFactory");
        try {
            Configuration configuration = new Configuration();

            // Load properties from external config
            Properties hibernateProps = new Properties();
            hibernateProps.put("hibernate.connection.driver_class", props.getProperty("jdbc.driver"));
            hibernateProps.put("hibernate.connection.url", props.getProperty("jdbc.url"));
            hibernateProps.put("hibernate.connection.username", props.getProperty("jdbc.user", ""));
            hibernateProps.put("hibernate.connection.password", props.getProperty("jdbc.pass", ""));

            hibernateProps.put("hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect");
            hibernateProps.put("hibernate.hbm2ddl.auto", "update"); // or "validate"
            hibernateProps.put("hibernate.show_sql", "true");
            hibernateProps.put("hibernate.format_sql", "true");

            configuration.setProperties(hibernateProps);

            // Register annotated entity classes
            configuration.addAnnotatedClass(UserHibernate.class);
            configuration.addAnnotatedClass(ParticipantHibernate.class);
            configuration.addAnnotatedClass(RaceHibernate.class);
            configuration.addAnnotatedClass(ParticipantRaceHibernate.class);

            logger.info("Building Hibernate SessionFactory");
            return configuration.buildSessionFactory();

        } catch (Exception e) {
            logger.error("Failed to initialize Hibernate", e);
            throw new RuntimeException("Error creating Hibernate SessionFactory", e);
        }
    }

    public static void closeSessionFactory() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            logger.info("Closing Hibernate SessionFactory");
            sessionFactory.close();
        }
    }
}
