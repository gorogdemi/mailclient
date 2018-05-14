package hu.unideb.inf.project.email.dao;

import hu.unideb.inf.project.email.dao.api.AccountDAO;
import hu.unideb.inf.project.email.model.Account;
import hu.unideb.inf.project.email.utility.EntityManagerFactoryUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Impelemtation of the {@code AccountDAO} interface.
 */
public class AccountDAOImpl implements AccountDAO, AutoCloseable {

    private EntityManager entityManager;

    /**
     * Constructor to construct the {@code AccountDAOImpl} object.
     */
    public AccountDAOImpl() {
        EntityManagerFactory entityManagerFactory = EntityManagerFactoryUtil.getInstance().getEntityManagerFactory();
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Account> getAllAccount() {
        TypedQuery<Account> query = entityManager.createQuery("SELECT a FROM hu.unideb.inf.project.email.model.Account a", Account.class);
        return query.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void persist(Account entity) {
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.getTransaction().commit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Account findById(Integer id) {
        return entityManager.find(Account.class, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Account entity) {
        Account account = findById(entity.getId());
        entityManager.getTransaction().begin();
        entityManager.remove(account);
        entityManager.getTransaction().commit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Account entity) {
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
