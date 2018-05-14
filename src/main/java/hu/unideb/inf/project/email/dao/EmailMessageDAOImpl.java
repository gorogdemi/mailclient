package hu.unideb.inf.project.email.dao;

import hu.unideb.inf.project.email.dao.api.EmailMessageDAO;
import hu.unideb.inf.project.email.model.EmailMessage;
import hu.unideb.inf.project.email.model.MailboxFolder;
import hu.unideb.inf.project.email.utility.EntityManagerFactoryUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Impelemtation of the {@code EmailMessageDAO} interface.
 */
public class EmailMessageDAOImpl implements EmailMessageDAO, AutoCloseable {

    private EntityManager entityManager;

    /**
     * Constructor to construct the {@code EmailMessageDAOImpl} object.
     */
    public EmailMessageDAOImpl() {
        EntityManagerFactory entityManagerFactory = EntityManagerFactoryUtil.getInstance().getEntityManagerFactory();
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EmailMessage> getAllEmailMessage() {
        TypedQuery<EmailMessage> query = entityManager.createQuery("SELECT m FROM hu.unideb.inf.project.email.model.EmailMessage m WHERE m.deleted = 0", EmailMessage.class);
        return query.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void persist(EmailMessage entity) {
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.getTransaction().commit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EmailMessage findById(Integer id) {
        return entityManager.find(EmailMessage.class, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(EmailMessage entity) {
        EmailMessage message = findById(entity.getId());
        entityManager.getTransaction().begin();
        entityManager.remove(message);
        entityManager.getTransaction().commit();
    }

    @Override
    public void update(EmailMessage entity) {
        entityManager.getTransaction().begin();
        entityManager.merge(entity);
        entityManager.getTransaction().commit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void persistMessages(List<EmailMessage> messages) {
        entityManager.getTransaction().begin();
        for (EmailMessage message : messages)
            entityManager.persist(message);
        entityManager.getTransaction().commit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAllUid(MailboxFolder folder) {
        Query query = entityManager.createQuery("SELECT m.uid FROM hu.unideb.inf.project.email.model.EmailMessage m WHERE folder_id = " + folder.getId());
        return query.getResultList();
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
