package hu.unideb.inf.project.email.dao.api;

import hu.unideb.inf.project.email.model.Account;

import java.util.List;

public interface AccountDAO extends GenericDAO<Account, Integer> {
    List<Account> getAllAccount();
}
