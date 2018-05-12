package hu.unideb.inf.project.email.controller;

import hu.unideb.inf.project.email.model.Account;
import hu.unideb.inf.project.email.model.EmailMessage;
import hu.unideb.inf.project.email.service.EmailService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Arrays;

public class SendEmailController {

    private Account account;
    private EmailService emailService;

    @FXML
    private Label senderLabel;

    @FXML
    private TextField recipientField;

    @FXML
    private TextField ccField;

    @FXML
    private TextField bccField;

    @FXML
    private TextField subjectField;

    @FXML
    private TextArea bodyField;

    public void initialize(Account account, EmailService emailService, EmailMessage message) {
        this.account = account;
        this.emailService = emailService;
        senderLabel.setText(account.getEmailAddress());
        if (message != null) {
            try {
                InternetAddress address = new InternetAddress(message.getRecipients());
                recipientField.setText(address.getAddress());
                if (message.getCc() != null)
                    ccField.setText(message.getCc());
                if (message.getBcc() != null)
                    bccField.setText(message.getBcc());
                subjectField.setText(message.getSubject());
                bodyField.setText(message.getBody());
            }
            catch (AddressException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleSend() {
        try {
            emailService.sendEmail(Arrays.asList(InternetAddress.parse(recipientField.getText())), Arrays.asList(InternetAddress.parse(ccField.getText())),
                    Arrays.asList(InternetAddress.parse(bccField.getText())), subjectField.getText(), bodyField.getText());
        } catch (AddressException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hiba");
            alert.setHeaderText("Érvénytelen mezőadatok!");
            alert.setContentText("Ügyeljen arra, hogy helyesen gépelje be az e-mail címeket.");
            alert.showAndWait();
        }
        Stage stage = (Stage) senderLabel.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) senderLabel.getScene().getWindow();
        stage.close();
    }
}
