package hu.unideb.inf.project.email.controller;

import hu.unideb.inf.project.email.app.MainApp;
import hu.unideb.inf.project.email.model.Account;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.jsoup.helper.StringUtil;

public class AccountSettingsController {

    private Account account;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField smtpServerAddressField;

    @FXML
    private TextField smtpServerPortField;

    @FXML
    private TextField pop3ServerAddressField;

    @FXML
    private TextField pop3ServerPortField;

    @FXML
    private CheckBox secureConnectionField;

    AccountSettingsController() {

    }

    AccountSettingsController(Account account) {
        this.account = account;
    }

    @FXML
    private void initialize() {
        if (account != null) {
            nameField.setText(account.getName());
            emailField.setText(account.getEmailAddress());
            usernameField.setText(account.getUserName());
            passwordField.setText(account.getPassword());
            smtpServerAddressField.setText(account.getSmtpServerAddress());
            smtpServerPortField.setText(Integer.toString(account.getSmtpServerPort()));
            pop3ServerAddressField.setText(account.getPop3ServerAddress());
            pop3ServerPortField.setText(Integer.toString(account.getPop3ServerPort()));
            secureConnectionField.setSelected(account.isSecure());
        }
    }

    @FXML
    private void handleOK() {
        if (validateFields()) {
            if (account != null) {
                account.setName(nameField.getText());
                account.setEmailAddress(emailField.getText());
                account.setUserName(usernameField.getText());
                account.setPassword(passwordField.getText());
                account.setSmtpServerAddress(smtpServerAddressField.getText());
                account.setSmtpServerPort(Integer.parseInt(smtpServerPortField.getText()));
                account.setPop3ServerAddress(pop3ServerAddressField.getText());
                account.setPop3ServerPort(Integer.parseInt(pop3ServerPortField.getText()));
                account.setSecure(secureConnectionField.isSelected());
                MainApp.ACCOUNT_SERVICE.modifyAccount(account);
            } else {
                MainApp.ACCOUNT_SERVICE.addAccount(new Account(nameField.getText(), emailField.getText(), usernameField.getText(), passwordField.getText(), smtpServerAddressField.getText(),
                        pop3ServerAddressField.getText(), Integer.parseInt(smtpServerPortField.getText()), Integer.parseInt(pop3ServerPortField.getText()), secureConnectionField.isSelected()));
            }
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.close();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hiba");
            alert.setHeaderText("Egy vagy több mező tartalma üres vagy érvénytelen!");
            alert.setContentText("Minden mezőt töltsön ki, és a port mezőkbe csak számokat írjon.");
            alert.showAndWait();
        }
    }

    private boolean validateFields() {
        if (nameField.getText().equals("") || emailField.getText().equals("") || usernameField.getText().equals("") || passwordField.getText().equals("") || smtpServerAddressField.getText().equals("") ||
                smtpServerPortField.getText().equals("") || pop3ServerAddressField.getText().equals("") || pop3ServerPortField.getText().equals(""))
            return false;
        return StringUtil.isNumeric(smtpServerPortField.getText()) && StringUtil.isNumeric(pop3ServerPortField.getText());
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
