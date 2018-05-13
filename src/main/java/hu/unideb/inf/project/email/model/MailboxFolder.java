package hu.unideb.inf.project.email.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    public MailboxFolder() {
    }

    public MailboxFolder(String name, Account account) {
        this.name = name;
        this.account = account;
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

    public List<EmailMessage> getMessages() {
        if (messages == null)
            messages = new ArrayList<>();
        return messages;
    }
}
