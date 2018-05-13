package hu.unideb.inf.project.email.dao.api;

import hu.unideb.inf.project.email.model.EmailMessage;
import hu.unideb.inf.project.email.model.MailboxFolder;

import java.util.List;

public interface EmailMessageDAO extends GenericDAO<EmailMessage, Integer> {
    List<EmailMessage> getAllEmailMessage();
    void persistMessages(List<EmailMessage> messages);
    List<String> getAllUid(MailboxFolder folder);
}