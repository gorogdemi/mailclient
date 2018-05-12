package hu.unideb.inf.project.email.dao;

import hu.unideb.inf.project.email.dao.api.EmailMessageDAO;
import hu.unideb.inf.project.email.model.Account;
import hu.unideb.inf.project.email.model.EmailMessage;
import hu.unideb.inf.project.email.model.MailboxFolder;
import hu.unideb.inf.project.email.utility.EntityManagerFactoryUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

public class EmailMessageDAOImpl implements EmailMessageDAO, AutoCloseable {

    private EntityManager entityManager;

    public EmailMessageDAOImpl() {
        EntityManagerFactory entityManagerFactory = EntityManagerFactoryUtil.getInstance().getEntityManagerFactory();
        this.entityManager = entityManagerFactory.createEntityManager();
    }
    @Override
    public List<EmailMessage> getAllEmailMessage() {
        TypedQuery<EmailMessage> query = entityManager.createQuery("SELECT m FROM hu.unideb.inf.project.email.model.EmailMessage m WHERE m.deleted = 0", EmailMessage.class);
        return query.getResultList();
    }

    @Override
    public void persist(EmailMessage entity) {
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.getTransaction().commit();
    }

    @Override
    public EmailMessage findById(Integer id) {
        return entityManager.find(EmailMessage.class, id);
    }

    @Override
    public void remove(EmailMessage entity) {
        entityManager.getTransaction().begin();
        entityManager.remove(entity);
        entityManager.getTransaction().commit();
    }

    public int getMaxUid() {
        Query query = entityManager.createQuery("SELECT MAX(m.uid) FROM hu.unideb.inf.project.email.model.EmailMessage m");
        return query.getResultList().get(0) == null ? -1 : (int)query.getResultList().get(0);
    }

    @Override
    public void close() {
        if (entityManager != null) {
            entityManager.close();
        }
    }
}
