package hu.unideb.inf.project.email.dao.api;

import java.io.Serializable;

public interface GenericDAO<T, ID> extends Serializable {

    void persist(T entity);
    T findById(ID id);
    void remove(T entity);

}
