package by.nikiforova.crud.util;

import by.nikiforova.crud.entity.User;
import org.hibernate.SessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;


public class HibernateUtil {

    private static final SessionFactory sessionFactory;
    private static final Logger logger = LogManager.getLogger();

    static {
        try {
            Configuration configuration = new Configuration().configure();

            configuration.addAnnotatedClass(by.nikiforova.crud.entity.User.class);


            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);

            logger.debug("Creating SessionFactory for working with DB");
        } catch (Throwable ex) {
            logger.error("Error of initialization SessionFactory");
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
            sessionFactory.close();
    }
}




