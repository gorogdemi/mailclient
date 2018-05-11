package hu.unideb.inf.project.email.service;

import hu.unideb.inf.project.email.dao.MailboxFolderDAOImpl;
import hu.unideb.inf.project.email.dao.api.MailboxFolderDAO;
import hu.unideb.inf.project.email.model.Account;
import hu.unideb.inf.project.email.model.EmailMessage;
import hu.unideb.inf.project.email.model.MailboxFolder;
import hu.unideb.inf.project.email.utility.EntityManagerFactoryUtil;
import org.jsoup.helper.StringUtil;

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

    public void moveMessage(EmailMessage message, MailboxFolder destination) {
        //TODO
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
