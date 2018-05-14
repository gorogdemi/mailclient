package hu.unideb.inf.project.email.service;

import hu.unideb.inf.project.email.dao.MailboxFolderDAOImpl;
import hu.unideb.inf.project.email.dao.api.MailboxFolderDAO;
import hu.unideb.inf.project.email.model.Account;
import hu.unideb.inf.project.email.model.MailboxFolder;

/**
 * Service class for handlig mailbox folders.
 */
public class FolderService {

    private Account account;
    private MailboxFolderDAO dao;

    /**
     * The default constructor which create a {@code FolderService} object.
     * Loads all mailbox entities from the database to the account list.
     * Creates system folders (INBOX, Sent Items, Deleted Items) if necessary.
     *
     * @param account an {@code Account} to manipulate its folders
     */
    public FolderService(Account account) {
        this.account = account;
        createSystemFoldersIfNotExist();
    }

    /**
     * Constructor for unit testing.
     * A fake DAO can be used.
     *
     * @param account an {@code Account} object to manipulate its folders
     * @param dao a fake {@code MailboxFolderDAO} object
     */
    public FolderService(Account account, MailboxFolderDAO dao) {
        this.account = account;
        this.dao = dao;
        createSystemFoldersIfNotExist();
    }

    /**
     * Method for getting a {@code MailboxFolder} object from the folders of account list based on the argument.
     *
     * @param folderName the name of the searched folder
     * @return a {@code MailboxFolder} object with name matching the argument
     */
    public MailboxFolder getFolderByName(String folderName) {
        return account.getFolders() != null ? account.getFolders().stream().filter(x -> x.getName().equals(folderName)).findFirst().orElse(null) : null;
    }

    /**
     * Method for getting the INBOX folder.
     *
     * @return a {@code MailboxFolder} object with name "INBOX" from the account's folder list
     */
    public MailboxFolder getInboxFolder() {
        return getSystemFolder("INBOX");
    }

    /**
     * Method for getting the Sent Items folder.
     *
     * @return a {@code MailboxFolder} object with name "Sent Items" from the account's folder list
     */
    public MailboxFolder getSentFolder() {
        return getSystemFolder("Sent Items");
    }

    /**
     * Method for getting the Deleted Items folder.
     *
     * @return a {@code MailboxFolder} object with name "Deleted Items" from the account's folder list
     */
    public MailboxFolder getDeletedFolder() {
        return getSystemFolder("Deleted Items");
    }

    /**
     * Method for adding a {@code MailboxFolder} object to the folder list and the database.
     *
     * @param folder the {@code MailboxFolder} object to add
     */
    public void addFolder(MailboxFolder folder) {
        MailboxFolderDAO dao = getDao();
        dao.persist(folder);
        dao.close();
        account.getFolders().add(folder);
    }

    /**
     * Method for modifying and persist an {@code MailboxFolder} object.
     *
     * @param folder the {@code MailboxFolder} object to modify
     */
    public void modifyFolder(MailboxFolder folder) {
        MailboxFolderDAO dao = getDao();
        folder.setName(folder.getName());
        dao.update(folder);
        dao.close();
    }

    /**
     * Method for deleting an {@code MailboxFolder} object from the folder list and the database.
     *
     * @param folder the {@code MailboxFolder} object to delete
     */
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
