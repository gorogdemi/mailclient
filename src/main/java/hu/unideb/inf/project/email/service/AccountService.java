package hu.unideb.inf.project.email.service;

import hu.unideb.inf.project.email.app.MainApp;
import hu.unideb.inf.project.email.dao.AccountDAOImpl;
import hu.unideb.inf.project.email.dao.api.AccountDAO;
import hu.unideb.inf.project.email.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Service class for handlig accounts.
 */
public class AccountService {

    private List<Account> accounts;
    private AccountDAO dao;
    private static Logger logger = LoggerFactory.getLogger(MainApp.class);

    /**
     * The default constructor which create an {@code AccountService} object.
     * Loads all account entities from the database to the account list.
     */
    public AccountService() {
        AccountDAO dao = new AccountDAOImpl();
        accounts = dao.getAllAccount();
        dao.close();
    }

    /**
     * Constructor for unit testing.
     * A fake DAO can be used.
     *
     * @param accounts the account list which will be used by the service
     * @param dao a fake Data Access Object
     */
    public AccountService(List<Account> accounts, AccountDAO dao) {
        this.accounts = accounts;
        this.dao = dao;
    }

    /**
     * Method for getting an {@code Account} object from the account list based on the argument.
     *
     * @param email the searched e-mail address
     * @return an {@code Account} object with e-mail address matching the argument
     */
    public Account getAccountByEmail(String email) {
        return accounts.stream().filter(x -> x.getEmailAddress().equals(email)).findFirst().orElse(null);
    }

    /**
     * Method for getting the account count of the list
     *
     * @return the account list size
     */
    public int getAccountCount() {
        return accounts.size();
    }

    /**
     * Getter for the account list.
     * @return a list of accounts
     */
    public List<Account> getAccounts() {
        return accounts;
    }

    /**
     * Method for adding an {@code Account} object to the account list and the database.
     *
     * @param account the {@code Account} object to add
     */
    public void addAccount(Account account) {
        AccountDAO dao = getDao();
        dao.persist(account);
        dao.close();
        accounts.add(account);
        logger.debug("Created account {}", account.getEmailAddress());
    }

    /**
     * Method for modifying and persist an {@code Account} object.
     *
     * @param account the {@code Account} object to modify
     */
    public void modifyAccount(Account account) {
        AccountDAO dao = getDao();
        dao.update(account);
        dao.close();
        logger.debug("Account {} modified.", account.getEmailAddress());
    }

    /**
     * Method for deleting an {@code Account} object from the account list and the database.
     *
     * @param account the {@code Account} object to delete
     */
    public void deleteAccount(Account account) {
        AccountDAO dao = getDao();
        dao.remove(account);
        dao.close();
        accounts.remove(account);
        logger.warn("Account with e-mail address {} has been deleted.", account.getEmailAddress());
    }

    private AccountDAO getDao() {
        return dao == null ? new AccountDAOImpl() : dao;
    }
}
