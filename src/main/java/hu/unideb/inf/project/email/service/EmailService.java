package hu.unideb.inf.project.email.service;

import hu.unideb.inf.project.email.dao.EmailMessageDAOImpl;
import hu.unideb.inf.project.email.dao.api.EmailMessageDAO;
import hu.unideb.inf.project.email.model.Account;
import hu.unideb.inf.project.email.model.EmailMessage;
import hu.unideb.inf.project.email.model.MailboxFolder;
import hu.unideb.inf.project.email.utility.api.MailingUtil;
import hu.unideb.inf.project.email.utility.MailingUtilImpl;

import javax.mail.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class EmailService {

    private Account account;
    private FolderService folderService;
    private EmailMessageDAO dao;
    private MailingUtil mailingUtil;

    public EmailService(Account account, FolderService folderService) {
        this.account = account;
        this.folderService = folderService;
        this.mailingUtil = new MailingUtilImpl(account, folderService);
        receiveAndStoreEmails();
    }

    public EmailService(Account account, FolderService folderService, EmailMessageDAO dao, MailingUtil mailingUtil) {
        this.account = account;
        this.folderService = folderService;
        this.dao = dao;
        this.mailingUtil = mailingUtil;
    }

    public void sendEmail(List<Address> to, List<Address> cc, List<Address> bcc, String subject, String body) {
        saveToSent(mailingUtil.sendEmail(to, cc, bcc, subject, body));
    }

    public EmailMessage getReplyMessage(EmailMessage message) {
        return new EmailMessage(null, message.getSender(), null, null, null,
            "RE: " + message.getSubject(), "\n\n------------------------------------" + message.getBody(),false, false, "-1", null);

    }

    public EmailMessage getReplyToAllMessage(EmailMessage message) {
        String recipients = getRecipientsWithoutOwnAddress(message.getRecipients());
        return new EmailMessage(null, message.getSender() + (!recipients.equals("") ?  "," + recipients : ""),
                message.getCc(), null, null,"RE: " + message.getSubject(), "\n\n------------------------------------" + message.getBody(),
                false, false, "-1", null);
    }

    public EmailMessage getForwardMessage(EmailMessage message) {
        return new EmailMessage(null, null, null, null, null,
                "FW: " + message.getSubject(), "\n\n------------------------------------" + message.getBody(), false, false, "-1", null);
    }

    public void deleteEmail(EmailMessage message, MailboxFolder source) {
        if (source == folderService.getDeletedFolder()) {
            EmailMessageDAO dao = getDao();
            message.setDeleted(true);
            dao.update(message);
            dao.close();
            source.getMessages().remove(message);
        }
        else
            moveEmail(message, source, folderService.getDeletedFolder());
    }

    public void moveEmail(EmailMessage message, MailboxFolder source, MailboxFolder destination) {
        if (source != destination) {
            EmailMessageDAO dao = getDao();
            message.setFolder(destination);
            dao.update(message);
            dao.close();
            source.getMessages().remove(message);
            destination.getMessages().add(message);
        }
    }

    public void setRead(EmailMessage message) {
        EmailMessageDAO dao = getDao();
        message.setRead(true);
        dao.update(message);
        dao.close();
    }

    public List<EmailMessage> searchEmails(List<EmailMessage> messages, String filterText, Boolean isRead) {
        return messages.stream().filter(x -> (x.getRecipients().toLowerCase().contains(filterText)
                || (x.getCc() != null && x.getCc().toLowerCase().contains(filterText)) || x.getSender().toLowerCase().contains(filterText)
                || x.getSubject().toLowerCase().contains(filterText) || x.getBody().toLowerCase().contains(filterText)) && (isRead == null || x.isRead() == isRead)).collect(Collectors.toList());
    }

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
            //TODO Log
            System.out.println("==============================");
            System.out.println("Subject: " + message.getSubject());
            System.out.println("From: " + message.getSender());
            System.out.println("Body: " + message.getBody());
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
