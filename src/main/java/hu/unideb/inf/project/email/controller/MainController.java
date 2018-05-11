package hu.unideb.inf.project.email.controller;

import hu.unideb.inf.project.email.app.MainApp;
import hu.unideb.inf.project.email.model.Account;
import hu.unideb.inf.project.email.model.MailboxFolder;
import hu.unideb.inf.project.email.service.EmailService;
import hu.unideb.inf.project.email.service.FolderService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.stream.Collectors;

public class MainController {

    private Account account;
    private EmailService emailService;
    private FolderService folderService;

    @FXML
    private WebView webBrowser;

    @FXML
    private Label emailLabel;

    @FXML
    private TextField searchField;

    @FXML
    private ListView<String> folderView;

    @FXML
    private ComboBox<String> filterBox;

    public void initialize(Account account) {
        this.account = account;
        this.folderService = new FolderService(account);
        this.emailService = new EmailService(account, folderService);
        emailLabel.setText(account.getEmailAddress());
        folderView.setItems(account.getFolders().stream().map(MailboxFolder::getName).collect(Collectors.toCollection(FXCollections::observableArrayList)));
    }

    @FXML
    private void initialize() {
        filterBox.getItems().addAll("Mind", "Olvasatlan", "Olvasott", "Jelölt", "Jelöletlen");
    }

    @FXML
    private void handleNewEmail() {
        System.out.println(account.getUserName());
        //TODO
    }

    @FXML
    private void handleAccounts() {
        Parent parent = null;
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/view/AccountSelectWindow.fxml"));
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setTitle("Fiók kiválasztása");
        stage.setScene(new Scene(parent));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.showAndWait();
        AccountSelectController controller = loader.getController();
        if (controller.getSelectedEmail() != null)
            account = MainApp.ACCOUNT_SERVICE.getAccountByEmail(controller.getSelectedEmail());
    }
}
