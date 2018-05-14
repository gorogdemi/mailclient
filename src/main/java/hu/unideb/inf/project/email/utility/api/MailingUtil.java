package hu.unideb.inf.project.email.utility.api;

import hu.unideb.inf.project.email.model.EmailMessage;
import hu.unideb.inf.project.email.model.MailboxFolder;

import javax.mail.Address;
import javax.mail.Message;
import java.util.List;

/**
 * Interface for a mailing utility.
 */
public interface MailingUtil {

    /**
     * Method for sending an e-mail message with an SMTP server.
     *
     * @param to a string list of the recipients' e-mail addresses
     * @param cc a string list of the CC's e-mail addresses
     * @param bcc a string list of the BCC's e-mail addresses
     * @param subject the subject string
     * @param body the body string
     * @return a {@code Message} object to work with
     */
    Message sendEmail(List<Address> to, List<Address> cc, List<Address> bcc, String subject, String body);

    /**
     * Method for getting e-mail messages from an e-mail server.
     * Only those messages will be returned which UID's are not in the specified UID list.
     *
     * @param uids a list of strings containing the UIDs already downloaded
     * @return a list of {@code EmailMessage} objects which UID's are not in the specified UID list.
     */
    List<EmailMessage> receiveEmails(List<String> uids);


    /**
     * Method for convert a {@code Message} object to an {@code EmailMessage} object.
     *
     * @param message a {@code Message} object to convert
     * @param isRead a boolean to set the isRead property of the {@code EmailMessage} object
     * @param uid a string to set the uid property of the {@code EmailMessage} object
     * @param folder a {@code MailboxFolder} object to set the folder property of the {@code EmailMessage} object
     * @return an {@code EmailMessage} object based on the arguments
     */
    EmailMessage convertMessageToEmailMessage(Message message, boolean isRead, String uid, MailboxFolder folder);
}
