package hu.unideb.inf.project.email.utility;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerFactoryUtil implements AutoCloseable {

    private static final EntityManagerFactoryUtil singleton = new EntityManagerFactoryUtil();
    private static final String PERSISTENCE_UNIT_NAME = "email-persistence-unit";
    private EntityManagerFactory entityManagerFactory;

    private EntityManagerFactoryUtil() {}

    public static EntityManagerFactoryUtil getInstance() {
        return singleton;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        return entityManagerFactory;
    }

    @Override
    public void close() throws Exception {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }
}
