package hu.unideb.inf.project.email.service;

import hu.unideb.inf.project.email.app.MainApp;
import hu.unideb.inf.project.email.dao.EmailMessageDAOImpl;
import hu.unideb.inf.project.email.dao.api.EmailMessageDAO;
import hu.unideb.inf.project.email.model.Account;
import hu.unideb.inf.project.email.model.EmailMessage;
import hu.unideb.inf.project.email.model.MailboxFolder;
import hu.unideb.inf.project.email.utility.MailingUtilImpl;
import hu.unideb.inf.project.email.utility.api.MailingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Address;
import javax.mail.Message;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for handlig e-mails.
 */
public class EmailService {

    private Account account;
    private FolderService folderService;
    private EmailMessageDAO dao;
    private MailingUtil mailingUtil;
    private static Logger logger = LoggerFactory.getLogger(MainApp.class);

    /**
     * The default constructor which create an {@code EmailService} object.
     * Loads all message entities from the database to the message list of a folder.
     * Downloads new messages from the server.
     *
     * @param account an {@code Account} to manipulate its folders
     * @param folderService a {@code FolderService} object for handling folders
     */
    public EmailService(Account account, FolderService folderService) {
        this.account = account;
        this.folderService = folderService;
        this.mailingUtil = new MailingUtilImpl(account, folderService);
        receiveAndStoreEmails();
    }

    /**
     * Constructor for unit testing.
     * A fake DAO and mailing utility can be used.
     *
     * @param account an {@code Account} object to manipulate its folders
     * @param folderService a {@code FolderService} object for handling folders
     * @param dao a fake {@code EmailMessageDAO} object
     * @param mailingUtil a fake {@code MailingUtil}
     */
    public EmailService(Account account, FolderService folderService, EmailMessageDAO dao, MailingUtil mailingUtil) {
        this.account = account;
        this.folderService = folderService;
        this.dao = dao;
        this.mailingUtil = mailingUtil;
    }

    /**
     * Method for sending and e-mail message.
     * Also save the message to the Sent Items folder.
     *
     * @param to a string list of the recipients' e-mail addresses
     * @param cc a string list of the CC's e-mail addresses
     * @param bcc a string list of the BCC's e-mail addresses
     * @param subject the subject string
     * @param body the body string
     */
    public void sendEmail(List<Address> to, List<Address> cc, List<Address> bcc, String subject, String body) {
        saveToSent(mailingUtil.sendEmail(to, cc, bcc, subject, body));
    }

    /**
     * Method for getting a reply message from an e-mail message.
     *
     * @param message an {@code EmailMessage} object to create a reply message from it
     * @return an {@code EmailMessage} object created from the argument message
     */
    public EmailMessage getReplyMessage(EmailMessage message) {
        return new EmailMessage(null, message.getSender(), null, null, null,
            "RE: " + message.getSubject(), "\n\n------------------------------------" + message.getBody(),false, false, "-1", null);

    }

    /**
     * Method for getting a reply-to-all message from an e-mail message.
     *
     * @param message an {@code EmailMessage} object to create a reply-to-all message from it
     * @return an {@code EmailMessage} object created from the argument message
     */
    public EmailMessage getReplyToAllMessage(EmailMessage message) {
        String recipients = getRecipientsWithoutOwnAddress(message.getRecipients());
        return new EmailMessage(null, message.getSender() + (!recipients.equals("") ?  "," + recipients : ""),
                message.getCc(), null, null,"RE: " + message.getSubject(), "\n\n------------------------------------" + message.getBody(),
                false, false, "-1", null);
    }

    /**
     * Method for getting a forward message from an e-mail message.
     *
     * @param message an {@code EmailMessage} object to create a forward message from it
     * @return an {@code EmailMessage} object created from the argument message
     */
    public EmailMessage getForwardMessage(EmailMessage message) {
        return new EmailMessage(null, null, null, null, null,
                "FW: " + message.getSubject(), "\n\n------------------------------------" + message.getBody(), false, false, "-1", null);
    }

    /**
     * Method for deleting an {@code EmailMessage} object from a folder.
     * If the source folder is the Deleted Items, then the message will be deleted permanently.
     *
     * @param message an {@code EmailMessage} object to delete
     * @param source the source {@code MailboxFolder} object of the message
     */
    public void deleteEmail(EmailMessage message, MailboxFolder source) {
        if (source == folderService.getDeletedFolder()) {
            EmailMessageDAO dao = getDao();
            message.setDeleted(true);
            dao.update(message);
            dao.close();
            source.getMessages().remove(message);
            logger.debug("Message with ID {} deleted from Deleted Items", message.getId());
        }
        else {
            moveEmail(message, source, folderService.getDeletedFolder());
            logger.debug("Message with ID {} moved from {} to Deleted Items", message.getId(), source.getName());
        }
    }

    /**
     * Method for moving an {@code EmailMessage} object to another folder.
     *
     * @param message an {@code EmailMessage} object to move
     * @param source the source {@code MailboxFolder} object of the message
     * @param destination the destination {@code MailboxFolder} object of the message
     */
    public void moveEmail(EmailMessage message, MailboxFolder source, MailboxFolder destination) {
        if (source != destination) {
            EmailMessageDAO dao = getDao();
            message.setFolder(destination);
            dao.update(message);
            dao.close();
            source.getMessages().remove(message);
            destination.getMessages().add(message);
            logger.debug("Message with ID {} moved from {} to {}", message.getId(), source.getName(), destination.getName());
        }
    }

    /**
     * Method for setting the isRead property true of an {@code EmailMessage} object and persist it.
     *
     * @param message an {@code EmailMessage} object to set the read property true
     */
    public void setRead(EmailMessage message) {
        EmailMessageDAO dao = getDao();
        message.setRead(true);
        dao.update(message);
        dao.close();
    }

    /**
     * Method for searching e-mail messages in a list by filters.
     * It applies the filter string all the string properties of the messages.
     *
     * @param messages the list of {@code EmailMessage} objects to search in
     * @param filterText a filter string
     * @param isRead a boolean to filter only read messages or not
     * @return a list of {@code EmailMessage} which contains messages fitting to filters
     */
    public List<EmailMessage> searchEmails(List<EmailMessage> messages, String filterText, Boolean isRead) {
        return messages.stream().filter(x -> (x.getRecipients().toLowerCase().contains(filterText)
                || (x.getCc() != null && x.getCc().toLowerCase().contains(filterText)) || x.getSender().toLowerCase().contains(filterText)
                || x.getSubject().toLowerCase().contains(filterText) || x.getBody().toLowerCase().contains(filterText)) && (isRead == null || x.isRead() == isRead)).collect(Collectors.toList());
    }

    /**
     * Method for getting e-mail messages to a folder from the database.
     * If the argument folder is the INBOX, then messages will be downloaded from the server too.
     *
     * @param folder a {@code MailboxFolder} object to fill the messages in
     */
    public void refreshEmails(MailboxFolder folder) {
        if (folder == folderService.getInboxFolder())
            receiveAndStoreEmails();
    }

    private void receiveAndStoreEmails() {
        EmailMessageDAO dao = getDao();
        List<EmailMessage> messageList = mailingUtil.receiveEmails(dao.getAllUid(folderService.getInboxFolder()));
        dao.persistMessages(messageList);
        dao.close();
        folderService.getInboxFolder().getMessages().addAll(messageList);
        for (EmailMessage message : messageList) {
            logger.debug("==============================");
            logger.debug("From: {}", message.getSender());
            logger.debug("Subject: {}", message.getSubject());
            logger.debug("Body: {}", message.getBody());
        }
    }

    private String getRecipientsWithoutOwnAddress(String recipients) {
        StringBuilder sb = new StringBuilder();
        for (String email : recipients.split(","))
            if (!email.equals(account.getEmailAddress()))
                sb.append(email).append(',');
        return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : "";
    }

    private void saveToSent(Message message) {
        EmailMessage emailMessage = mailingUtil.convertMessageToEmailMessage(message, true, "-1", folderService.getSentFolder());
        EmailMessageDAO dao = getDao();
        dao.persist(emailMessage);
        dao.close();
        folderService.getSentFolder().getMessages().add(emailMessage);
    }

    private EmailMessageDAO getDao() {
        return dao == null ? new EmailMessageDAOImpl() : dao;
    }
}
