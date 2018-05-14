package hu.unideb.inf.project.email.test;

import hu.unideb.inf.project.email.dao.api.AccountDAO;
import hu.unideb.inf.project.email.model.Account;
import hu.unideb.inf.project.email.service.AccountService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AccountServiceTest {

    private AccountService accountService;
    private List<Account> accounts;
    private Account gipszJakab;
    private Account kissPista;

    private void initialize() {
        accounts = new ArrayList<>();
        gipszJakab = new Account("Gipsz Jakab", "gipsz.jakab@proba.hu", "gibsz.jakab",
                "password", "smtp.proba.hu", "pop3.proba.hu", 587, 110, false);
        kissPista = new Account("Kiss István", "picipista@test.com", "picipista",
                "123456", "smtp.test.com", "pop.test.com", 465, 995, true);
        accounts.add(gipszJakab);
        accounts.add(kissPista);
        AccountDAO dao = new AccountDAO() {
            @Override
            public List<Account> getAllAccount() {
                return null;
            }

            @Override
            public void persist(Account entity) {
                if (entity == null)
                    throw new NullPointerException();
            }

            @Override
            public Account findById(Integer integer) {
                return null;
            }

            @Override
            public void remove(Account entity) {
                if (entity == null)
                    throw new NullPointerException();
            }

            @Override
            public void update(Account entity) {
                if (entity == null)
                    throw new NullPointerException();
                entity.setName("Test");
            }

            @Override
            public void close() {
            }
        };
        accountService = new AccountService(accounts, dao);
    }

    @Test
    public void testGetAccountByEmail() {
        initialize();
        assertEquals(gipszJakab, accountService.getAccountByEmail("gipsz.jakab@proba.hu"));
        assertEquals(kissPista, accountService.getAccountByEmail("picipista@test.com"));
        assertNull(accountService.getAccountByEmail("teszt@teszt.hu"));
        assertNull(accountService.getAccountByEmail(null));
    }

    @Test
    public void testGetAccountCount() {
        initialize();
        assertEquals(2, accountService.getAccountCount());
        assertEquals(accounts.size(), accountService.getAccountCount());
    }

    @Test
    public void testAddAccount() {
        initialize();
        Account test = new Account("Teszt", "test@test.com", "test",
                "test", "smtp.test.com", "pop3.test.com", 25, 100, false);
        accountService.addAccount(test);
        assertEquals(3, accounts.size());
        assertEquals(test, accounts.get(2));
        assertTrue(accounts.contains(test));
        assertThrows(Exception.class, () -> accountService.addAccount(null));
    }

    @Test
    public void testModifyAccount() {
        initialize();
        Account test = accounts.get(1);
        accountService.modifyAccount(test);
        assertEquals("Test", test.getName());
        assertThrows(Exception.class, () -> accountService.modifyAccount(null));
    }

    @Test
    public void testDeleteAccount() {
        initialize();
        Account test = accounts.get(1);
        accountService.deleteAccount(test);
        assertEquals(1, accounts.size());
        assertFalse(accounts.contains(test));
        assertThrows(Exception.class, () -> accountService.deleteAccount(null));
    }
}
