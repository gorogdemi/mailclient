package hu.unideb.inf.project.email.service;

import hu.unideb.inf.project.email.dao.EmailMessageDAOImpl;
import hu.unideb.inf.project.email.dao.MailboxFolderDAOImpl;
import hu.unideb.inf.project.email.model.Account;
import hu.unideb.inf.project.email.model.EmailMessage;
import hu.unideb.inf.project.email.model.MailboxFolder;

public class FolderService {

    private Account account;

    public FolderService(Account account) {
        this.account = account;
        createSystemFoldersIfNotExist();
    }

    public MailboxFolder getFolder(String folderName) {
        return account.getFolders().stream().filter(x -> x.getName().equals(folderName)).findFirst().orElse(null);
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

    public void moveMessage(EmailMessage message, MailboxFolder source, MailboxFolder destination) {
        if (source != destination) {
            EmailMessageDAOImpl dao = new EmailMessageDAOImpl();
            EmailMessage newMessage = dao.findById(message.getId());
            newMessage.setFolder(destination);
            dao.persist(newMessage);
            dao.close();
            source.getMessages().remove(message);
            destination.getMessages().add(newMessage);
        }
    }

    public void addFolder(String folderName) {
        MailboxFolder folder = new MailboxFolder(folderName, account);
        MailboxFolderDAOImpl dao = new MailboxFolderDAOImpl();
        dao.persist(folder);
        dao.close();
        account.getFolders().add(folder);
    }

    public void modifyFolder(MailboxFolder modified) {
        MailboxFolderDAOImpl dao = new MailboxFolderDAOImpl();
        MailboxFolder newFolder = dao.findById(modified.getId());
        newFolder.setName(modified.getName());
        dao.persist(newFolder);
        dao.close();
    }

    public void deleteFolder(MailboxFolder folder) {
        MailboxFolderDAOImpl dao = new MailboxFolderDAOImpl();
        dao.remove(dao.findById(folder.getId()));
        dao.close();
        account.getFolders().remove(folder);
    }

    private MailboxFolder getSystemFolder(String name) {
        MailboxFolder folder = getFolder(name);
        if (folder == null) {
            folder = new MailboxFolder(name, account);
            MailboxFolderDAOImpl dao = new MailboxFolderDAOImpl();
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
}
