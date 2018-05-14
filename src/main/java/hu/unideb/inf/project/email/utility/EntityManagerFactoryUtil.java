package hu.unideb.inf.project.email.utility;

import hu.unideb.inf.project.email.app.MainApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for handling {@code EntityManagerFactory} objects.
 */
public class EntityManagerFactoryUtil implements AutoCloseable {

    private static final EntityManagerFactoryUtil singleton = new EntityManagerFactoryUtil();
    private static final String PERSISTENCE_UNIT_NAME = "email-persistence-unit";
    private static final String CREATE_PERSISTENCE_UNIT_NAME = "email-persistence-unit-create";
    private static Logger logger = LoggerFactory.getLogger(MainApp.class);
    private EntityManagerFactory entityManagerFactory;

    private EntityManagerFactoryUtil() {}

    /**
     * Method for getting the singleton instance.
     *
     * @return the {@code EntityManagerFactoryUtil} object
     */
    public static EntityManagerFactoryUtil getInstance() {
        return singleton;
    }

    /**
     * Method for getting an {@code EntityManagerFactory} object.
     * Creates the object if necessary.
     * Creates the database file if necessary;
     *
     * @return an {@code EntityManagerFactory} object
     */
    public EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            File db = new File("email.db");
            if (!db.exists()) {
                entityManagerFactory = Persistence.createEntityManagerFactory(CREATE_PERSISTENCE_UNIT_NAME);
                try {
                    DriverManager.getConnection("jdbc:sqlite:email.db");
                    logger.info("Created database file.");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            else
                entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        return entityManagerFactory;
    }

    /**
     * Method for closing the {@code EntityManagerFactory} objects.
     */
    @Override
    public void close() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
            logger.debug("An EntityManagerFactory instance has been closed.");
        }
    }
}
