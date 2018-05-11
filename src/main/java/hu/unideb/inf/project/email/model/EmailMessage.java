package hu.unideb.inf.project.email.model;

import org.hibernate.annotations.Generated;

import javax.mail.Address;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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

    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    private MailboxFolder folder;

    public EmailMessage() {
    }

    public EmailMessage(String sender, String recipients, String cc, String bcc, LocalDateTime time, String subject, String body) {
        this.sender = sender;
        this.recipients = recipients;
        this.cc = cc;
        this.bcc = bcc;
        this.time = time;
        this.subject = subject;
        this.body = body;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipients() {
        return recipients;
    }

    public void setRecipients(String recipients) {
        this.recipients = recipients;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
