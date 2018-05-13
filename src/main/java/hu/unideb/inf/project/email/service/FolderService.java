package hu.unideb.inf.project.email.service;

import hu.unideb.inf.project.email.dao.MailboxFolderDAOImpl;
import hu.unideb.inf.project.email.dao.api.MailboxFolderDAO;
import hu.unideb.inf.project.email.model.Account;
import hu.unideb.inf.project.email.model.MailboxFolder;

public class FolderService {

    private Account account;
    private MailboxFolderDAO dao;

    public FolderService(Account account) {
        this.account = account;
        createSystemFoldersIfNotExist();
    }

    public FolderService(Account account, MailboxFolderDAO dao) {
        this.account = account;
        this.dao = dao;
        createSystemFoldersIfNotExist();
    }

    public MailboxFolder getFolderByName(String folderName) {
        return account.getFolders() != null ? account.getFolders().stream().filter(x -> x.getName().equals(folderName)).findFirst().orElse(null) : null;
    }

    public MailboxFolder getInboxFolder() {
        return getSystemFolder("INBOX");
    }

    public MailboxFolder getSentFolder() {
        return getSystemFolder("Sent Items");
    }

    public MailboxFolder getDeletedFolder() {
        return getSystemFolder("Deleted Items");
    }

    public void addFolder(MailboxFolder folder) {
        MailboxFolderDAO dao = getDao();
        dao.persist(folder);
        dao.close();
        account.getFolders().add(folder);
    }

    public void modifyFolder(MailboxFolder folder) {
        MailboxFolderDAO dao = getDao();
        folder.setName(folder.getName());
        dao.update(folder);
        dao.close();
    }

    public void deleteFolder(MailboxFolder folder) {
        MailboxFolderDAO dao = getDao();
        dao.remove(folder);
        dao.close();
        account.getFolders().remove(folder);
    }

    private MailboxFolder getSystemFolder(String name) {
        MailboxFolder folder = getFolderByName(name);
        if (folder == null) {
            folder = new MailboxFolder(name, account);
            MailboxFolderDAO dao = getDao();
            dao.persist(folder);
            dao.close();
            account.getFolders().add(folder);
        }
        return folder;
    }

    private void createSystemFoldersIfNotExist() {
        getInboxFolder();
        getSentFolder();
        getDeletedFolder();
    }

    private MailboxFolderDAO getDao() {
        return dao == null ? new MailboxFolderDAOImpl() : dao;
    }
}
