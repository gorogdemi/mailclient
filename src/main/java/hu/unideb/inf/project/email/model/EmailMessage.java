package hu.unideb.inf.project.email.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Model class which represents e-mail message data.
 */
@Entity
public class EmailMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String sender;

    private String recipients;

    private String cc;

    private String bcc;

    private LocalDateTime time;

    private String subject;

    @Column(length = 2048)
    private String body;

    private boolean isRead;

    private boolean isDeleted;

    private String uid;

    @ManyToOne(fetch = FetchType.LAZY)
    private MailboxFolder folder;

    /**
     * The default constructor. Constructs an empty {@code EmailMessage} object.
     */
    public EmailMessage() {
    }

    /**
     * Parametered constructor.
     *
     * @param sender the sender of the message
     * @param recipients the recipients of the message
     * @param cc the CCs of the message
     * @param bcc the BCCs of the message
     * @param time the sent time of the message
     * @param subject the subject of the message
     * @param body the body of the message
     * @param isRead boolean of read state
     * @param isDeleted boolean of deleted state
     * @param uid uid
     * @param folder a {@code MailboxFolder} object
     */
    public EmailMessage(String sender, String recipients, String cc, String bcc, LocalDateTime time, String subject, String body, boolean isRead, boolean isDeleted, String uid, MailboxFolder folder) {
        this.sender = sender;
        this.recipients = recipients;
        this.cc = cc;
        this.bcc = bcc;
        this.time = time;
        this.subject = subject;
        this.body = body;
        this.isRead = isRead;
        this.isDeleted = isDeleted;
        this.uid = uid;
        this.folder = folder;
    }

    /**
     * Getter for the ID.
     *
     * @return ID
     */
    public int getId() {
        return id;
    }

    /**
     * Setter for ID.
     *
     * @param id ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter for the Sender.
     *
     * @return Sender
     */
    public String getSender() {
        return sender;
    }

    /**
     * Getter for the Recipients.
     *
     * @return Recipients
     */
    public String getRecipients() {
        return recipients;
    }

    /**
     * Getter for the CC.
     *
     * @return CC
     */
    public String getCc() {
        return cc;
    }

    /**
     * Getter for the BCC.
     *
     * @return BCC
     */
    public String getBcc() {
        return bcc;
    }

    /**
     * Getter for the Sent time.
     *
     * @return Sent time
     */
    public LocalDateTime getTime() {
        return time;
    }

    /**
     * Getter for the Subject.
     *
     * @return Subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Setter for Subject.
     *
     * @param subject Subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Getter for the Body.
     *
     * @return Body
     */
    public String getBody() {
        return body;
    }

    /**
     * Getter for the Read state.
     *
     * @return Read state
     */
    public boolean isRead() {
        return isRead;
    }

    /**
     * Setter for Read state.
     *
     * @param read Read state
     */
    public void setRead(boolean read) {
        isRead = read;
    }

    /**
     * Getter for the Deleted state.
     *
     * @return Deleted state
     */
    public boolean isDeleted() {
        return isDeleted;
    }

    /**
     * Setter for Deleted state.
     *
     * @param deleted Deleted state
     */
    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    /**
     * Getter for the containing folder.
     *
     * @return a {@code MailboxFolder} object
     */
    public MailboxFolder getFolder() {
        return folder;
    }

    /**
     * Setter for containing folder.
     *
     * @param folder a {@code MailboxFolder} object
     */
    public void setFolder(MailboxFolder folder) {
        this.folder = folder;
    }
}
