package hu.unideb.inf.project.email.service;

import hu.unideb.inf.project.email.dao.AccountDAOImpl;
import hu.unideb.inf.project.email.dao.api.AccountDAO;
import hu.unideb.inf.project.email.model.Account;
import hu.unideb.inf.project.email.utility.EntityManagerFactoryUtil;

import java.util.List;

public class AccountService {

    private List<Account> accounts;

    public AccountService() {
        AccountDAOImpl dao = new AccountDAOImpl();
        accounts = dao.getAllAccount();
        dao.close();
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
        AccountDAOImpl dao = new AccountDAOImpl();
        dao.persist(account);
        dao.close();
        accounts.add(account);
    }

    public void modifyAccount(Account modified) {
        AccountDAOImpl dao = new AccountDAOImpl();
        Account account = dao.findById(modified.getId());
        account.setName(modified.getName());
        account.setEmailAddress(modified.getEmailAddress());
        account.setUserName(modified.getUserName());
        account.setPassword(modified.getPassword());
        account.setSmtpServerAddress(modified.getSmtpServerAddress());
        account.setSmtpServerPort(modified.getSmtpServerPort());
        account.setPop3ServerAddress(modified.getPop3ServerAddress());
        account.setPop3ServerPort(modified.getPop3ServerPort());
        account.setSecure(modified.isSecure());
        dao.persist(account);
        dao.close();
    }

    public void deleteAccount(Account account) {
        AccountDAOImpl dao = new AccountDAOImpl();
        dao.remove(dao.findById(account.getId()));
        dao.close();
        accounts.remove(account);
    }
}
