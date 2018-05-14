package hu.unideb.inf.project.email.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class which represents mailbox folder data.
 */
@Entity
public class MailboxFolder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "folder")
    private List<EmailMessage> messages;

    /**
     * The default constructor. Constructs an empty {@code MailboxFolder} object.
     */
    public MailboxFolder() {
    }

    /**
     * Parametered constructor.
     *
     * @param name name of the folder
     * @param account an {@code Account} object which the folder belongs to
     */
    public MailboxFolder(String name, Account account) {
        this.name = name;
        this.account = account;
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
     * Getter for the message list.
     *
     * @return a list of {@code EmailMessage} objects
     */
    public List<EmailMessage> getMessages() {
        if (messages == null)
            messages = new ArrayList<>();
        return messages;
    }
}
