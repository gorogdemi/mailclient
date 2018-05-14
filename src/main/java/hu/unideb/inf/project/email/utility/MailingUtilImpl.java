package hu.unideb.inf.project.email.utility;

import com.sun.mail.pop3.POP3Folder;
import hu.unideb.inf.project.email.model.Account;
import hu.unideb.inf.project.email.model.EmailMessage;
import hu.unideb.inf.project.email.model.MailboxFolder;
import hu.unideb.inf.project.email.service.FolderService;
import hu.unideb.inf.project.email.utility.api.MailingUtil;
import org.jsoup.Jsoup;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.SearchTerm;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Implementation of the {@code MailingUtil} interface.
 */
public class MailingUtilImpl implements MailingUtil {

    private Account account;
    private Session emailSession;
    private FolderService folderService;

    /**
     * Constructor to construct an {@code MailingUtilImpl} object.
     *
     * @param account an {@code Account} object to work with
     * @param folderService a {@code FolderService} object to handle the account's folders
     */
    public MailingUtilImpl(Account account, FolderService folderService) {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "pop3");
        properties.put("mail.pop3.host", account.getPop3ServerAddress());
        properties.put("mail.pop3.port", account.getPop3ServerPort());
        properties.put("mail.pop3.ssl.enable", account.isSecure());
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", account.isSecure());
        properties.put("mail.smtp.host", account.getSmtpServerAddress());
        properties.put("mail.smtp.port", account.getSmtpServerPort());
        properties.put("mail.mime.charset", "utf-8");
        this.account = account;
        this.emailSession = Session.getDefaultInstance(properties);
        this.folderService = folderService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message sendEmail(List<Address> to, List<Address> cc, List<Address> bcc, String subject, String body) {
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
            return message;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EmailMessage> receiveEmails(List<String> uids) {
        try {
            Store emailStore = emailSession.getStore("pop3");
            emailStore.connect(account.getUserName(), account.getPassword());
            POP3Folder emailFolder = (POP3Folder) emailStore.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            SearchTerm term = new SearchTerm() {
                @Override
                public boolean match(Message message) {
                    try {
                        return (uids == null || uids.isEmpty()) || !uids.contains(emailFolder.getUID(message));
                    } catch (MessagingException ex) {
                        ex.printStackTrace();
                    }
                    return false;
                }
            };

            FetchProfile fp = new FetchProfile();
            fp.add(UIDFolder.FetchProfileItem.UID);
            Message[] newMessages = (uids == null || uids.isEmpty()) ? emailFolder.getMessages() : emailFolder.search(term);
            emailFolder.fetch(newMessages, fp);
            List<EmailMessage> messageList = new ArrayList<>();
            for (int i = 0, j = 1; i < newMessages.length && j <= 30; i++, j++) {
                Message message = newMessages[newMessages.length - j];
                EmailMessage emailMessage = convertMessageToEmailMessage(message, false, emailFolder.getUID(message), folderService.getInboxFolder());
                messageList.add(emailMessage);
            }
            emailFolder.close(false);
            emailStore.close();
            return messageList;
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public EmailMessage convertMessageToEmailMessage(Message message, boolean isRead, String uid, MailboxFolder folder) {
        try {
            return new EmailMessage(convertAddressArrayToString(message.getFrom()),
                    convertAddressArrayToString(message.getRecipients(Message.RecipientType.TO)),
                    message.getRecipients(Message.RecipientType.CC) != null ? convertAddressArrayToString(message.getRecipients(Message.RecipientType.CC)) : null,
                    message.getRecipients(Message.RecipientType.BCC) != null ? convertAddressArrayToString(message.getRecipients(Message.RecipientType.BCC)) : null,
                    convertDateToLocalDateTime(message.getSentDate()), message.getSubject(), getTextFromMessage(message), isRead, false, uid, folder);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendMessage(Message message) throws MessagingException {
        Transport transport = emailSession.getTransport("smtp");
        transport.connect(account.getUserName(), account.getPassword());
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }

    private Date getCurrentDateTime() {
        LocalDateTime time = LocalDateTime.now();
        return new Date(time.getYear() - 1900, time.getMonth().getValue() - 1, time.getDayOfMonth(), time.getHour(), time.getMinute(), time.getSecond());
    }

    private LocalDateTime convertDateToLocalDateTime(Date date) {
        return date != null ? LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()) : LocalDateTime.now();
    }

    private String convertAddressArrayToString(Address[] addresses) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(addresses).forEach(x -> {
            try {
                sb.append(new InternetAddress(x.toString()).getAddress()).append(',');
            } catch (AddressException e) {
                e.printStackTrace();
            }
        });
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
