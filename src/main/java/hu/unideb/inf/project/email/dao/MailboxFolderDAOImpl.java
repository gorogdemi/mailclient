package hu.unideb.inf.project.email.dao;

import hu.unideb.inf.project.email.dao.api.MailboxFolderDAO;
import hu.unideb.inf.project.email.model.MailboxFolder;
import hu.unideb.inf.project.email.utility.EntityManagerFactoryUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

public class MailboxFolderDAOImpl implements MailboxFolderDAO, AutoCloseable {

    private EntityManager entityManager;

    public MailboxFolderDAOImpl() {
        EntityManagerFactory entityManagerFactory = EntityManagerFactoryUtil.getInstance().getEntityManagerFactory();
        this.entityManager = entityManagerFactory.createEntityManager();
    }
    @Override
    public List<MailboxFolder> getAllMailboxFolder() {
        TypedQuery<MailboxFolder> query = entityManager.createQuery("SELECT f FROM hu.unideb.inf.project.email.model.MailboxFolder f", MailboxFolder.class);
        return query.getResultList();
    }

    @Override
    public void persist(MailboxFolder entity) {
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.getTransaction().commit();
    }

    @Override
    public MailboxFolder findById(Integer id) {
        return entityManager.find(MailboxFolder.class, id);
    }

    @Override
    public void remove(MailboxFolder entity) {
        MailboxFolder folder = findById(entity.getId());
        entityManager.getTransaction().begin();
        entityManager.remove(folder);
        entityManager.getTransaction().commit();
    }

    @Override
    public void update(MailboxFolder entity) {
        entityManager.getTransaction().begin();
        entityManager.merge(entity);
        entityManager.getTransaction().commit();
    }

    @Override
    public void close() {
        if (entityManager != null) {
            entityManager.close();
        }
    }
}
