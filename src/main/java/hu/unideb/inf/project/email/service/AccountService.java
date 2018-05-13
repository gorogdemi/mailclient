package hu.unideb.inf.project.email.service;

import hu.unideb.inf.project.email.dao.AccountDAOImpl;
import hu.unideb.inf.project.email.dao.api.AccountDAO;
import hu.unideb.inf.project.email.model.Account;

import java.util.List;

public class AccountService {

    private List<Account> accounts;
    private AccountDAO dao;

    public AccountService() {
        AccountDAO dao = new AccountDAOImpl();
        accounts = dao.getAllAccount();
        dao.close();
    }

    public AccountService(List<Account> accounts, AccountDAO dao) {
        this.accounts = accounts;
        this.dao = dao;
    }

    public Account getAccountByEmail(String email) {
        return accounts.stream().filter(x -> x.getEmailAddress().equals(email)).findFirst().orElse(null);
    }

    public int getAccountCount() {
        return accounts.size();
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void addAccount(Account account) {
        AccountDAO dao = getDao();
        dao.persist(account);
        dao.close();
        accounts.add(account);
    }

    public void modifyAccount(Account account) {
        AccountDAO dao = getDao();
        dao.update(account);
        dao.close();
    }

    public void deleteAccount(Account account) {
        AccountDAO dao = getDao();
        dao.remove(account);
        dao.close();
        accounts.remove(account);
    }

    private AccountDAO getDao() {
        return dao == null ? new AccountDAOImpl() : dao;
    }
}
