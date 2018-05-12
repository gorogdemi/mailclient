package hu.unideb.inf.project.email.service;

import com.sun.mail.pop3.POP3Folder;
import hu.unideb.inf.project.email.dao.EmailMessageDAOImpl;
import hu.unideb.inf.project.email.model.Account;
import hu.unideb.inf.project.email.model.EmailMessage;
import hu.unideb.inf.project.email.model.MailboxFolder;
import org.jsoup.Jsoup;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.SearchTerm;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EmailService {

    private Account account;
    private FolderService folderService;
    private Session emailSession;

    public EmailService(Account account, FolderService folderService) {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "pop3");
        properties.put("mail.pop3.host", account.getPop3ServerAddress());
        properties.put("mail.pop3.port", account.getPop3ServerPort());
        properties.put("mail.pop3.starttls.enable", account.isSecure());
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", account.isSecure());
        properties.put("mail.smtp.host", account.getSmtpServerAddress());
        properties.put("mail.smtp.port", account.getSmtpServerPort());
        properties.put("mail.mime.charset", "utf-8");

        this.emailSession = Session.getDefaultInstance(properties);
        this.account = account;
        this.folderService = folderService;
    }

    public void sendEmail(List<Address> to, List<Address> cc, List<Address> bcc, String subject, String body) {
        try {
            MimeMessage message = new MimeMessage(emailSession);
            message.setFrom(String.format("%s <%s>", account.getName(), account.getEmailAddress()));
            message.setRecipients(Message.RecipientType.TO, (to.toArray(new Address[0])));
            if (cc != null)
                message.setRecipients(Message.RecipientType.CC, (cc.toArray(new Address[0])));
            if (bcc != null)
                message.setRecipients(Message.RecipientType.BCC, (bcc.toArray(new Address[0])));
            message.setSentDate(getCurrentDateTime());
            message.setSubject(subject);
            message.setText(body);
            sendMessage(message);
            saveToSent(message);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }

    public EmailMessage getReplyMessage(EmailMessage message) {
        return new EmailMessage(account.getEmailAddress(), message.getSender(), null, null, null,
            "RE: " + message.getSubject(), "\n\n------------------------------------\n" + message.getBody(),false, false, -1, null);

    }

    public EmailMessage getReplyToAllMessage(EmailMessage message) {
        return new EmailMessage(account.getEmailAddress(), message.getSender() + ";" + getRecipientsWithoutOwnAddress(message.getRecipients()),
                message.getCc(), null, null,"RE: " + message.getSubject(), "\n\n------------------------------------\n" + message.getBody(), false, false, -1, null);
    }

    public EmailMessage getForwardMessage(EmailMessage message) {
        return new EmailMessage(account.getEmailAddress(), null, null, null, null,
                "FW: " + message.getSubject(), "\n\n------------------------------------\n" + message.getBody(), false, false, -1, null);
    }

    public void deleteEmail(EmailMessage message, MailboxFolder source) {
        if (source == folderService.getDeletedFolder()) {
            EmailMessageDAOImpl dao = new EmailMessageDAOImpl();
            EmailMessage newMessage = dao.findById(message.getId());
            newMessage.setDeleted(true);
            dao.persist(newMessage);
            dao.close();
            source.getMessages().remove(message);
        }
        else
            folderService.moveMessage(message, source, folderService.getDeletedFolder());
    }

    public void setRead(EmailMessage message) {
        EmailMessageDAOImpl dao = new EmailMessageDAOImpl();
        EmailMessage newMessage = dao.findById(message.getId());
        newMessage.setRead(true);
        dao.persist(newMessage);
        dao.close();
    }

    public List<EmailMessage> searchEmails(List<EmailMessage> messages, String filterText, Boolean isRead) {
        return messages.stream().filter(x -> (x.getRecipients().toLowerCase().contains(filterText)
                || (x.getCc() != null && x.getCc().toLowerCase().contains(filterText)) || x.getSender().toLowerCase().contains(filterText)
                || x.getSubject().toLowerCase().contains(filterText) || x.getBody().toLowerCase().contains(filterText)) && (isRead == null || x.isRead() == isRead)).collect(Collectors.toList());
    }

    public List<EmailMessage> getEmailsFromFolder(MailboxFolder folder) {
        if (folder == folderService.getInboxFolder()) {
            receiveNewEmailsFromServer();
        }
        return folder.getMessagesWithoutDeleted();
    }

    private void receiveNewEmailsFromServer() {

        try {
            Store emailStore = emailSession.getStore("pop3");
            emailStore.connect(account.getUserName(), account.getPassword());
            POP3Folder emailFolder = (POP3Folder)emailStore.getFolder("INBOX");
            emailFolder.open(Folder.READ_WRITE);

            EmailMessageDAOImpl dao = new EmailMessageDAOImpl();
            int maxUid = dao.getMaxUid();

            SearchTerm term = new SearchTerm() {
                @Override
                public boolean match(Message message) {
                    try {
                        return Integer.parseInt(emailFolder.getUID(message)) > maxUid;
                    } catch (MessagingException ex) {
                        ex.printStackTrace();
                    }
                    return false;
                }
            };

            Message[] newMessages = emailFolder.search(term);
            for (int i = 0; i < newMessages.length; i++) {
                Message message = newMessages[i];
                EmailMessage emailMessage = convertMessageToEmailMessage(message, false, Integer.parseInt(emailFolder.getUID(message)), folderService.getInboxFolder());
                dao.persist(emailMessage);

                //TODO Log
                System.out.println("==============================");
                System.out.println("EmailMessage #" + (i + 1));
                System.out.println("UID: " + emailFolder.getUID(message));
                /*System.out.println("SEEN: " + message.isSet(Flags.Flag.SEEN));
                System.out.println("RECENT: " + message.isSet(Flags.Flag.RECENT));
                System.out.println("DELETED: " + message.isSet(Flags.Flag.DELETED));
                System.out.println("ANSWERED: " + message.isSet(Flags.Flag.ANSWERED));
                System.out.println("FLAGGED: " + message.isSet(Flags.Flag.FLAGGED));*/
                System.out.println("Subject: " + message.getSubject());
                System.out.println("From: " + message.getFrom()[0]);
                System.out.println("Text: " + getTextFromMessage(message));
            }

            dao.close();
            emailFolder.close(true);
            emailStore.close();
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }

    private String getRecipientsWithoutOwnAddress(String recipients) {
        StringBuilder sb = new StringBuilder();
        for (String email : recipients.split(","))
            if (!email.equals(account.getEmailAddress()))
                sb.append(email).append(',');
        return sb.toString();
    }

    private void sendMessage(Message message) throws MessagingException {
        Transport transport = emailSession.getTransport("smtp");
        transport.connect(account.getUserName(), account.getPassword());
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }

    private void saveToSent(Message message) throws MessagingException, IOException {
        EmailMessage emailMessage = convertMessageToEmailMessage(message, true, -1, folderService.getSentFolder());
        EmailMessageDAOImpl dao = new EmailMessageDAOImpl();
        dao.persist(emailMessage);
        dao.close();
        folderService.getSentFolder().getMessages().add(emailMessage);
    }

    private EmailMessage convertMessageToEmailMessage(Message message, boolean isRead, int uid, MailboxFolder folder) throws MessagingException, IOException {
        return new EmailMessage(InternetAddress.toString(message.getFrom()),
                convertAddressArrayToString(message.getRecipients(Message.RecipientType.TO)),
                message.getRecipients(Message.RecipientType.CC) != null ? convertAddressArrayToString(message.getRecipients(Message.RecipientType.CC)) : null,
                message.getRecipients(Message.RecipientType.BCC) != null ? convertAddressArrayToString(message.getRecipients(Message.RecipientType.BCC)) : null,
                LocalDateTime.now(), message.getSubject(), getTextFromMessage(message), isRead, false, uid, folder);
    }

    private Date getCurrentDateTime() {
        LocalDateTime time = LocalDateTime.now();
        return new Date(time.getYear() - 1900, time.getMonth().getValue() - 1, time.getDayOfMonth(), time.getHour(), time.getMinute(), time.getSecond());
    }

    private String convertAddressArrayToString(Address[] addresses) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(addresses).forEach(x -> sb.append(x.toString()).append(','));
        return sb.substring(0, sb.length() - 1);
    }

    private String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        }
        else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart)message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append("\n").append(bodyPart.getContent());
                break;
            }
            else if (bodyPart.isMimeType("text/html")) {
                String html = (String)bodyPart.getContent();
                result.append("\n").append(Jsoup.parse(html).text());
            }
            else if (bodyPart.getContent() instanceof MimeMultipart){
                result.append(getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent()));
            }
        }
        return result.toString();
    }
}
