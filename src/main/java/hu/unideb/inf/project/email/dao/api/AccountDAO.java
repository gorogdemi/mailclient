package hu.unideb.inf.project.email.dao.api;

import hu.unideb.inf.project.email.model.Account;

import java.util.List;

/**
 * Interface for {@code Account} Data Access Objects.
 */
public interface AccountDAO extends GenericDAO<Account, Integer> {

    /**
     * Method for getting all account entities form the database.
     *
     * @return a list of {@code Account} objects form the database
     */
    List<Account> getAllAccount();
}
