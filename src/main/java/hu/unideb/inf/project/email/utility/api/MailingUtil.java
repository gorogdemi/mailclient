package hu.unideb.inf.project.email.utility.api;

import hu.unideb.inf.project.email.model.EmailMessage;
import hu.unideb.inf.project.email.model.MailboxFolder;

import javax.mail.Address;
import javax.mail.Message;
import java.util.List;

public interface MailingUtil {
    Message sendEmail(List<Address> to, List<Address> cc, List<Address> bcc, String subject, String body);
    List<EmailMessage> receiveEmails(List<String> uids);
    EmailMessage convertMessageToEmailMessage(Message message, boolean isRead, String uid, MailboxFolder folder);
}
