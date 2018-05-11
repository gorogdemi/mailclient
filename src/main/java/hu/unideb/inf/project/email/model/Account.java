package hu.unideb.inf.project.email.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;

    private String emailAddress;

    private String userName;

    private String password;

    private String smtpServerAddress;

    private String pop3ServerAddress;

    private int smtpServerPort;

    private int pop3ServerPort;

    private boolean isSecure;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
    private List<MailboxFolder> folders;

    public Account() {
    }

    public Account(String name, String emailAddress, String userName, String password, String smtpServerAddress, String pop3ServerAddress, int smtpServerPort, int pop3ServerPort, boolean isSecure) {
        this.name = name;
        this.emailAddress = emailAddress;
        this.userName = userName;
        this.password = password;
        this.smtpServerAddress = smtpServerAddress;
        this.pop3ServerAddress = pop3ServerAddress;
        this.smtpServerPort = smtpServerPort;
        this.pop3ServerPort = pop3ServerPort;
        this.isSecure = isSecure;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSmtpServerAddress() {
        return smtpServerAddress;
    }

    public void setSmtpServerAddress(String smtpServerAddress) {
        this.smtpServerAddress = smtpServerAddress;
    }

    public String getPop3ServerAddress() {
        return pop3ServerAddress;
    }

    public void setPop3ServerAddress(String pop3ServerAddress) {
        this.pop3ServerAddress = pop3ServerAddress;
    }

    public int getSmtpServerPort() {
        return smtpServerPort;
    }

    public void setSmtpServerPort(int smtpServerPort) {
        this.smtpServerPort = smtpServerPort;
    }

    public int getPop3ServerPort() {
        return pop3ServerPort;
    }

    public void setPop3ServerPort(int pop3ServerPort) {
        this.pop3ServerPort = pop3ServerPort;
    }

    public boolean isSecure() {
        return isSecure;
    }

    public void setSecure(boolean secure) {
        isSecure = secure;
    }

    public List<MailboxFolder> getFolders() {
        if (folders == null)
            this.folders = new ArrayList<>();
        return folders;
    }
}
