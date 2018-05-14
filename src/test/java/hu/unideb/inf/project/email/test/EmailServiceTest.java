package hu.unideb.inf.project.email.test;

import hu.unideb.inf.project.email.dao.api.EmailMessageDAO;
import hu.unideb.inf.project.email.dao.api.MailboxFolderDAO;
import hu.unideb.inf.project.email.model.Account;
import hu.unideb.inf.project.email.model.EmailMessage;
import hu.unideb.inf.project.email.model.MailboxFolder;
import hu.unideb.inf.project.email.service.EmailService;
import hu.unideb.inf.project.email.service.FolderService;
import hu.unideb.inf.project.email.utility.api.MailingUtil;
import org.junit.jupiter.api.Test;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class EmailServiceTest {

    private Account account;
    private FolderService folderService;
    private EmailService emailService;

    private void initialize() {
        account = new Account("Gipsz Jakab", "gipsz.jakab@proba.hu", "gibsz.jakab",
                "password", "smtp.proba.hu", "pop3.proba.hu", 587, 110, false);
        EmailMessageDAO dao = new EmailMessageDAO() {
            @Override
            public List<EmailMessage> getAllEmailMessage() {
                return null;
            }

            @Override
            public void persistMessages(List<EmailMessage> messages) {
                if (messages == null)
                    throw new NullPointerException();
            }

            @Override
            public List<String> getAllUid(MailboxFolder folder) {
                return null;
            }

            @Override
            public void persist(EmailMessage entity) {
                if (entity == null)
                    throw new NullPointerException();
            }

            @Override
            public EmailMessage findById(Integer integer) {
                return null;
            }

            @Override
            public void remove(EmailMessage entity) {
                    if (entity == null)
                    throw new NullPointerException();
            }

            @Override
            public void update(EmailMessage entity) {
                if (entity == null)
                    throw new NullPointerException();
                entity.setSubject("Tárgy");
            }

            @Override
            public void close() {

            }
        };
        MailboxFolderDAO dao2 = new MailboxFolderDAO() {
            @Override
            public void persist(MailboxFolder entity) {
                if (entity == null)
                    throw new NullPointerException();
            }

            @Override
            public MailboxFolder findById(Integer integer) {
                return null;
            }

            @Override
            public void remove(MailboxFolder entity) {
                if (entity == null)
                    throw new NullPointerException();
            }

            @Override
            public void update(MailboxFolder entity) {
                if (entity == null)
                    throw new NullPointerException();
                entity.setName("Test");
            }

            @Override
            public void close() {

            }
        };
        MailingUtil mailingUtil = new MailingUtil() {
            @Override
            public Message sendEmail(List<Address> to, List<Address> cc, List<Address> bcc, String subject, String body) {
                try {
                    MimeMessage message = new MimeMessage(Session.getDefaultInstance(new Properties()));
                    message.setFrom(String.format("%s <%s>", account.getName(), account.getEmailAddress()));
                    message.setRecipients(Message.RecipientType.TO, (to.toArray(new Address[0])));
                    if (cc != null)
                        message.setRecipients(Message.RecipientType.CC, (cc.toArray(new Address[0])));
                    if (bcc != null)
                        message.setRecipients(Message.RecipientType.BCC, (bcc.toArray(new Address[0])));
                    message.setSentDate(getCurrentDateTime());
                    message.setSubject(subject);
                    message.setText(body);
                    return message;
                }
                catch (MessagingException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public List<EmailMessage> receiveEmails(List<String> uids) {
                List<EmailMessage> list = new ArrayList<>();
                EmailMessage message = emailService.getReplyMessage(new EmailMessage("x@y.com", "teszt@teszt.hu", null, null, null,
                        "TesztTárgy", "TesztTörzs", false, false, null, null));
                EmailMessage message2 = emailService.getReplyMessage(new EmailMessage("y@x.com", "teszt2@teszt.hu", null, null, null,
                        "TesztTárgy2", "TesztTörzs2", false, false, null, null));
                list.add(message);
                list.add(message2);
                return list;
            }

            @Override
            public EmailMessage convertMessageToEmailMessage(Message message, boolean isRead, String uid, MailboxFolder folder) {
                return new EmailMessage("x@y.com", "teszt@teszt.hu", null, null, null,
                        "TesztTárgy", "TesztTörzs", isRead, false, uid, folder);
            }

            private Date getCurrentDateTime() {
                LocalDateTime time = LocalDateTime.now();
                return new Date(time.getYear() - 1900, time.getMonth().getValue() - 1, time.getDayOfMonth(), time.getHour(), time.getMinute(), time.getSecond());
            }
        };
        folderService = new FolderService(account, dao2);
        emailService = new EmailService(account, folderService, dao, mailingUtil);
    }

    @Test
    public void testSendMail() {
        initialize();
        try {
            emailService.sendEmail(Arrays.asList(InternetAddress.parse("teszt@teszt.hu")), null, null, "TesztTárgy", "TesztTörzs");
        }
        catch (AddressException e) {
            e.printStackTrace();
        }
        EmailMessage message = folderService.getSentFolder().getMessages().get(0);
        assertNotNull(message);
        assertEquals("teszt@teszt.hu", message.getRecipients());
        assertNull(message.getCc());
        assertNull(message.getBcc());
        assertEquals("TesztTárgy", message.getSubject());
        assertEquals("TesztTörzs", message.getBody());
    }

    @Test
    public void testGetReplyMessage() {
        initialize();
        EmailMessage message = emailService.getReplyMessage(new EmailMessage("x@y.com", "teszt@teszt.hu", null, null, null,
                "TesztTárgy", "TesztTörzs", false, false, null, null));
        assertNull(message.getSender());
        assertEquals("x@y.com", message.getRecipients());
        assertNull(message.getCc());
        assertNull(message.getBcc());
        assertEquals("RE: TesztTárgy", message.getSubject());
        assertEquals("\n\n------------------------------------TesztTörzs", message.getBody());
        assertThrows(Exception.class, () -> emailService.getReplyMessage(null));
    }

    @Test
    public void testGetReplyToAllMessage() {
        initialize();
        EmailMessage message1 = emailService.getReplyToAllMessage(new EmailMessage("x@y.com", "gipsz.jakab@proba.hu", null, null, null,
                "TesztTárgy", "TesztTörzs", false, false, null, null));
        assertNull(message1.getSender());
        assertEquals("x@y.com", message1.getRecipients());
        assertNull(message1.getCc());
        assertNull(message1.getBcc());
        assertEquals("RE: TesztTárgy", message1.getSubject());
        assertEquals("\n\n------------------------------------TesztTörzs", message1.getBody());

        EmailMessage message2 = emailService.getReplyToAllMessage(new EmailMessage("x@y.com", "gipsz.jakab@proba.hu,teszt@teszt.hu", "teszt2@teszt.hu", null, null,
                "TesztTárgy", "TesztTörzs", false, false, null, null));
        assertNull(message2.getSender());
        assertEquals("x@y.com,teszt@teszt.hu", message2.getRecipients());
        assertEquals("teszt2@teszt.hu", message2.getCc());
        assertNull(message2.getBcc());
        assertEquals("RE: TesztTárgy", message2.getSubject());
        assertEquals("\n\n------------------------------------TesztTörzs", message2.getBody());
        assertThrows(Exception.class, () -> emailService.getReplyToAllMessage(null));
    }

    @Test
    public void testGetForwardMessage() {
        initialize();
        EmailMessage message = emailService.getForwardMessage(new EmailMessage("x@y.com", "teszt@teszt.hu", null, null, null,
                "TesztTárgy", "TesztTörzs", false, false, null, null));
        assertNull(message.getSender());
        assertNull(message.getRecipients());
        assertNull(message.getCc());
        assertNull(message.getBcc());
        assertEquals("FW: TesztTárgy", message.getSubject());
        assertEquals("\n\n------------------------------------TesztTörzs", message.getBody());
        assertThrows(Exception.class, () -> emailService.getForwardMessage(null));
    }

    @Test
    public void testDeleteEmail() {
        initialize();
        EmailMessage message = emailService.getForwardMessage(new EmailMessage("x@y.com", "teszt@teszt.hu", null, null, null,
                "TesztTárgy", "TesztTörzs", false, false, null, null));
        emailService.deleteEmail(message, folderService.getDeletedFolder());
        assertTrue(message.isDeleted());
        emailService.deleteEmail(message, folderService.getInboxFolder());
        assertEquals(folderService.getDeletedFolder(), message.getFolder());
        assertThrows(Exception.class, () -> emailService.deleteEmail(null, null));
    }

    @Test
    public void testMoveEmail() {
        initialize();
        EmailMessage message = emailService.getForwardMessage(new EmailMessage("x@y.com", "teszt@teszt.hu", null, null, null,
                "TesztTárgy", "TesztTörzs", false, false, null, null));
        folderService.getInboxFolder().getMessages().add(message);
        emailService.moveEmail(message, folderService.getInboxFolder(), folderService.getSentFolder());
        assertEquals(folderService.getSentFolder(), message.getFolder());
        assertEquals(0, folderService.getInboxFolder().getMessages().size());
        assertEquals(1, folderService.getSentFolder().getMessages().size());
        assertThrows(Exception.class, () -> emailService.moveEmail(null, null, folderService.getSentFolder()));
    }

    @Test
    public void testSetRead() {
        initialize();
        EmailMessage message = emailService.getForwardMessage(new EmailMessage("x@y.com", "teszt@teszt.hu", null, null, null,
                "TesztTárgy", "TesztTörzs", false, false, null, null));
        emailService.setRead(message);
        assertTrue(message.isRead());
        assertThrows(Exception.class, () -> emailService.setRead(null));
    }

    @Test
    public void testSearchEmails() {
        initialize();
        EmailMessage message = new EmailMessage("x@y.com", "teszt@teszt.hu", null, null, null,
                "TesztTárgy", "TesztTörzs", false, false, null, null);
        EmailMessage message2 = new EmailMessage("y@x.com", "teszt2@teszt.hu", null, null, null,
                "TesztTárgy2", "TesztTörzs2", false, false, null, null);
        EmailMessage message3 = new EmailMessage("x@y.com", "teszt@teszt.hu", "a@b.hu", null, null,
                "TesztTárgy", "TesztTörzs", false, false, null, null);
        EmailMessage message4 = new EmailMessage("y@x.com", "teszt2@teszt.hu", null, null, null,
                "TesztTárgy2", "TesztTörzs2", true, false, null, null);
        List<EmailMessage> list = new ArrayList<>();
        list.add(message);
        list.add(message2);
        list.add(message3);
        list.add(message4);
        assertEquals(1, emailService.searchEmails(list, "", true).size());
        assertEquals(4, emailService.searchEmails(list, "", null).size());
        assertEquals(1, emailService.searchEmails(list, "a@b", null).size());
        assertEquals(2, emailService.searchEmails(list, "teszt2", null).size());
        assertThrows(Exception.class, () -> emailService.searchEmails(list, null, null));
    }

    @Test
    public void testRefreshEmails() {
        initialize();
        emailService.refreshEmails(folderService.getInboxFolder());
        assertEquals(2, folderService.getInboxFolder().getMessages().size());
    }
}
