package hu.unideb.inf.project.email.test;

import hu.unideb.inf.project.email.dao.api.MailboxFolderDAO;
import hu.unideb.inf.project.email.model.Account;
import hu.unideb.inf.project.email.model.MailboxFolder;
import hu.unideb.inf.project.email.service.FolderService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FolderServiceTest {

    private Account account;
    private FolderService folderService;
    private MailboxFolder folder;

    private void initialize() {
        account = new Account("Gipsz Jakab", "gipsz.jakab@proba.hu", "gibsz.jakab",
                "password", "smtp.proba.hu", "pop3.proba.hu", 587, 110, false);
        folder = new MailboxFolder("Spam", account);
        MailboxFolderDAO dao = new MailboxFolderDAO() {
            @Override
            public List<MailboxFolder> getAllMailboxFolder() {
                return null;
            }

            @Override
            public void persist(MailboxFolder entity) {
                if (entity == null)
                    throw new NullPointerException();
            }

            @Override
            public MailboxFolder findById(Integer integer) {
                return null;
            }

            @Override
            public void remove(MailboxFolder entity) {
                if (entity == null)
                    throw new NullPointerException();
            }

            @Override
            public void update(MailboxFolder entity) {
                if (entity == null)
                    throw new NullPointerException();
                entity.setName("Test");
            }

            @Override
            public void close() {

            }
        };
        folderService = new FolderService(account, dao);
        account.getFolders().add(folder);
    }

    @Test
    public void testGetFolderByName() {
        initialize();
        assertEquals(folder, folderService.getFolderByName("Spam"));
        assertNull(folderService.getFolderByName(null));
    }

    @Test
    public void testGetInboxFolder() {
        initialize();
        assertEquals(account.getFolders().get(0), folderService.getInboxFolder());
    }

    @Test
    public void testGetSentFolder() {
        initialize();
        assertEquals(account.getFolders().get(1), folderService.getSentFolder());
    }

    @Test
    public void testGetDeletedFolder() {
        initialize();
        assertEquals(account.getFolders().get(2), folderService.getDeletedFolder());
    }

    @Test
    public void testAddFolder() {
        initialize();
        MailboxFolder test = new MailboxFolder("TesztMappa", account);
        folderService.addFolder(test);
        assertEquals(5, account.getFolders().size());
        assertEquals(test, account.getFolders().get(4));
        assertTrue(account.getFolders().contains(test));
        assertThrows(Exception.class, () -> folderService.addFolder(null));
    }

    @Test
    public void testModifyFolder() {
        initialize();
        MailboxFolder test = account.getFolders().get(1);
        folderService.modifyFolder(test);
        assertEquals("Test", test.getName());
        assertThrows(Exception.class, () -> folderService.modifyFolder(null));
    }

    @Test
    public void testDeleteFolder() {
        initialize();
        MailboxFolder test = account.getFolders().get(1);
        folderService.deleteFolder(test);
        assertEquals(3, account.getFolders().size());
        assertFalse(account.getFolders().contains(test));
        assertThrows(Exception.class, () -> folderService.deleteFolder(null));
    }
}
