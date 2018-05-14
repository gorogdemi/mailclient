package hu.unideb.inf.project.email.dao;

import hu.unideb.inf.project.email.dao.api.MailboxFolderDAO;
import hu.unideb.inf.project.email.model.MailboxFolder;
import hu.unideb.inf.project.email.utility.EntityManagerFactoryUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Impelemtation of the {@code MailboxFolderDAO} interface.
 */
public class MailboxFolderDAOImpl implements MailboxFolderDAO, AutoCloseable {

    private EntityManager entityManager;

    /**
     * Constructor to construct the {@code MailboxFolderDAOImpl} object.
     */
    public MailboxFolderDAOImpl() {
        EntityManagerFactory entityManagerFactory = EntityManagerFactoryUtil.getInstance().getEntityManagerFactory();
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void persist(MailboxFolder entity) {
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.getTransaction().commit();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public MailboxFolder findById(Integer id) {
        return entityManager.find(MailboxFolder.class, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(MailboxFolder entity) {
        MailboxFolder folder = findById(entity.getId());
        entityManager.getTransaction().begin();
        entityManager.remove(folder);
        entityManager.getTransaction().commit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(MailboxFolder entity) {
        entityManager.getTransaction().begin();
        entityManager.merge(entity);
        entityManager.getTransaction().commit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (entityManager != null) {
            entityManager.close();
        }
    }
}
