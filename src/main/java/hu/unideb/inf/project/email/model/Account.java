package hu.unideb.inf.project.email.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class which represents e-mail account data.
 */
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

    /**
     * The default constructor. Constructs an empty {@code Account} object.
     */
    public Account() {
    }

    /**
     * Parametered constructor.
     *
     * @param name display name
     * @param emailAddress address
     * @param userName username
     * @param password password
     * @param smtpServerAddress the IP address or domain name) of the SMTP server
     * @param pop3ServerAddress the IP address or domain name of the POP3 server
     * @param smtpServerPort port of the SMTP server
     * @param pop3ServerPort port of the POP3 server
     * @param isSecure requiring SSL connection
     */
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
     * Getter for the Name.
     *
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for Name.
     *
     * @param name Name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the E-mail address.
     *
     * @return E-mail address
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Setter for E-mail address.
     *
     * @param emailAddress E-mail address
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Getter for the Username.
     *
     * @return Username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Setter for Username.
     *
     * @param userName Username
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Getter for the Password.
     *
     * @return Password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter for Password.
     *
     * @param password Password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter for the SMTP server address.
     *
     * @return SMTP server address
     */
    public String getSmtpServerAddress() {
        return smtpServerAddress;
    }

    /**
     * Setter for SMTP server address.
     *
     * @param smtpServerAddress SMTP server address
     */
    public void setSmtpServerAddress(String smtpServerAddress) {
        this.smtpServerAddress = smtpServerAddress;
    }

    /**
     * Getter for the POP3 server address.
     *
     * @return POP3 server address
     */
    public String getPop3ServerAddress() {
        return pop3ServerAddress;
    }

    /**
     * Setter for POP3 server address.
     *
     * @param pop3ServerAddress POP3 server address
     */
    public void setPop3ServerAddress(String pop3ServerAddress) {
        this.pop3ServerAddress = pop3ServerAddress;
    }

    /**
     * Getter for the SMTP server port.
     *
     * @return SMTP server port
     */
    public int getSmtpServerPort() {
        return smtpServerPort;
    }

    /**
     * Setter for SMTP server port.
     *
     * @param smtpServerPort SMTP server port
     */
    public void setSmtpServerPort(int smtpServerPort) {
        this.smtpServerPort = smtpServerPort;
    }

    /**
     * Getter for the POP3 server port.
     *
     * @return POP3 server port
     */
    public int getPop3ServerPort() {
        return pop3ServerPort;
    }

    /**
     * Setter for POP3 server port.
     *
     * @param pop3ServerPort POP3 server port
     */
    public void setPop3ServerPort(int pop3ServerPort) {
        this.pop3ServerPort = pop3ServerPort;
    }

    /**
     * Getter for the Secure connection.
     *
     * @return Secure connection
     */
    public boolean isSecure() {
        return isSecure;
    }

    /**
     * Setter for Secure connection.
     *
     * @param secure Secure connection
     */
    public void setSecure(boolean secure) {
        isSecure = secure;
    }

    /**
     * Getter for the folders.
     *
     * @return list of {@code MailboxFolder} objects
     */
    public List<MailboxFolder> getFolders() {
        if (folders == null)
            this.folders = new ArrayList<>();
        return folders;
    }
}
