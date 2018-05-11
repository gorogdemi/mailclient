package hu.unideb.inf.project.email.dao.api;

import hu.unideb.inf.project.email.model.EmailMessage;

import java.util.List;

public interface EmailMessageDAO extends GenericDAO<EmailMessage, Integer> {
    List<EmailMessage> getAllEmailMessage();
}