package hu.unideb.inf.project.email.dao.api;

import java.io.Serializable;

/**
 * Generic interface for Data Access Objects.
 *
 * @param <T> the type of an entity
 * @param <ID> the type of the ID field
 */
public interface GenericDAO<T, ID> extends Serializable {

    /**
     * Method for persisting an entity.
     *
     * @param entity a {@code T} object to persist
     */
    void persist(T entity);

    /**
     * Method for finding an entity by id.
     *
     * @param id the id of the entity
     * @return a {@code T} object with id specified in the argument
     */
    T findById(ID id);

    /**
     * Method for removing an entity.
     *
     * @param entity a {@code T} object to remove
     */
    void remove(T entity);

    /**
     * Method for updating an entity.
     *
     * @param entity a {@code T} object to update
     */
    void update(T entity);

    /**
     * Method for closing an {@code EntityManager} object.
     */
    void close();
}
