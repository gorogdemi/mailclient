package hu.unideb.inf.project.email.dao.api;

import hu.unideb.inf.project.email.model.MailboxFolder;

import java.util.List;

public interface MailboxFolderDAO extends GenericDAO<MailboxFolder, Integer> {
    List<MailboxFolder> getAllMailboxFolder();
}
