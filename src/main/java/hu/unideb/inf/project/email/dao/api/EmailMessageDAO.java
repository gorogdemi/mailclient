package hu.unideb.inf.project.email.dao.api;

import hu.unideb.inf.project.email.model.EmailMessage;
import hu.unideb.inf.project.email.model.MailboxFolder;

import java.util.List;

/**
 * Interface for {@code EmailMessage} Data Access Objects.
 */
public interface EmailMessageDAO extends GenericDAO<EmailMessage, Integer> {

    /**
     * Method for getting all message entities form the database.
     *
     * @return a list of {@code EmailMessage} objects form the database
     */
    List<EmailMessage> getAllEmailMessage();

    /**
     * Method for persisting a list of messages.
     *
     * @param messages a list of {@code EmailMessage} objects to persist
     */
    void persistMessages(List<EmailMessage> messages);

    /**
     * Method for getting a list of all message UIDs in a folder from the database
     *
     * @param folder a {@code MailboxFolder} object the get the UIDs from
     * @return a list containing the UID strings
     */
    List<String> getAllUid(MailboxFolder folder);
}