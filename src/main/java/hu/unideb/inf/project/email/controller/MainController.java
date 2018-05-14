package hu.unideb.inf.project.email.controller;

import hu.unideb.inf.project.email.app.MainApp;
import hu.unideb.inf.project.email.model.Account;
import hu.unideb.inf.project.email.model.EmailMessage;
import hu.unideb.inf.project.email.model.MailboxFolder;
import hu.unideb.inf.project.email.service.EmailService;
import hu.unideb.inf.project.email.service.FolderService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MainController {

    private Account account;
    private EmailService emailService;
    private FolderService folderService;
    private MailboxFolder selectedFolder;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
    private List<EmailMessage> selectedFolderEmails;

    @FXML
    private TextArea emailView;

    @FXML
    private Label emailLabel;

    @FXML
    private TextField searchField;

    @FXML
    private ListView<String> folderView;

    @FXML
    private ListView<String> messageView;

    @FXML
    private Button answerButton;

    @FXML
    private Button answerAllButton;

    @FXML
    private Button forwardButton;

    @FXML
    private Button deleteEmailButton;

    @FXML
    private Button moveEmailButton;

    @FXML
    private Button modifyFolderButton;

    @FXML
    private Button deleteFolderButton;

    @FXML
    private ComboBox<String> filterBox;

    public void initialize(Account account) {
        this.account = account;
        this.folderService = new FolderService(account);
        this.emailService = new EmailService(account, folderService);
        emailLabel.setText(account.getEmailAddress());
        folderView.setItems(account.getFolders().stream().map(MailboxFolder::getName).collect(Collectors.toCollection(FXCollections::observableArrayList)));
        folderView.getSelectionModel().select(0);
    }

    @FXML
    private void initialize() {
        filterBox.getItems().addAll("Mind", "Olvasatlan", "Olvasott");
        filterBox.getSelectionModel().select(0);
        filterBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedFolderEmails = emailService.searchEmails(selectedFolder.getMessages(), searchField.getText().toLowerCase(), getBoolFromReadState(newValue))
                    .stream().sorted(Comparator.comparing(EmailMessage::getTime).reversed()).collect(Collectors.toList());
            setMessageViewItems();
        });
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            selectedFolderEmails = emailService.searchEmails(selectedFolder.getMessages(), newValue.toLowerCase(), getBoolFromReadState(filterBox.getSelectionModel().getSelectedItem()))
                    .stream().sorted(Comparator.comparing(EmailMessage::getTime).reversed()).collect(Collectors.toList());
            setMessageViewItems();
        });
        folderView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (folderView.getSelectionModel().getSelectedItem() != null) {
                modifyFolderButton.setDisable(false);
                deleteFolderButton.setDisable(false);
                selectedFolder = folderService.getFolderByName(folderView.getSelectionModel().getSelectedItem());
                selectedFolderEmails = selectedFolder.getMessages().stream().sorted(Comparator.comparing(EmailMessage::getTime).reversed()).collect(Collectors.toList());
                setMessageViewItems();
            }
            else {
                modifyFolderButton.setDisable(true);
                deleteFolderButton.setDisable(true);
            }
        });
        messageView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (messageView.getSelectionModel().getSelectedItem() != null) {
                answerButton.setDisable(false);
                answerAllButton.setDisable(false);
                forwardButton.setDisable(false);
                deleteEmailButton.setDisable(false);
                moveEmailButton.setDisable(false);
                StringBuilder sb = new StringBuilder();
                EmailMessage message = selectedFolderEmails.get(messageView.getSelectionModel().getSelectedIndex());
                sb.append("Feladó: ").append(message.getSender()).append('\n');
                sb.append("Címzett: ").append(message.getRecipients()).append('\n');
                if (message.getCc() != null)
                    sb.append("Másolatot kap: ").append(message.getCc()).append('\n');
                sb.append("Beérkezés ideje: ").append(message.getTime().format(FORMATTER)).append('\n');
                sb.append("Tárgy: ").append(message.getSubject()).append("\n------------------------------------------\n");
                sb.append(message.getBody());
                emailView.setText(sb.toString());
                if (!message.isRead()) {
                    emailService.setRead(message);
                    String item = messageView.getSelectionModel().getSelectedItem();
                    messageView.getItems().set(messageView.getSelectionModel().getSelectedIndex(), item.substring(5));
                }
            }
            else {
                answerButton.setDisable(true);
                answerAllButton.setDisable(true);
                forwardButton.setDisable(true);
                deleteEmailButton.setDisable(true);
                moveEmailButton.setDisable(true);
                emailView.setText("");
            }
        });
    }

    private Boolean getBoolFromReadState(String state) {
        if (state.equals("Mind"))
            return null;
        else return state.equals("Olvasott");
    }

    private void setMessageViewItems() {
        messageView.setItems(selectedFolderEmails.stream().filter(x -> !x.isDeleted())
                .map(x -> String.format("%s\n%s\n%s", !x.isRead() ? "[ÚJ] " + x.getSender() : x.getSender(), x.getSubject(), x.getTime().format(FORMATTER)))
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
    }

    @FXML
    private void handleRefresh() {
        emailService.refreshEmails(selectedFolder);
    }

    @FXML
    private void handleNewEmail() {
        Parent parent = null;
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/view/SendEmailWindow.fxml"));
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setTitle("E-mail küldése");
        stage.setScene(new Scene(parent));
        stage.initModality(Modality.APPLICATION_MODAL);
        SendEmailController controller = loader.getController();
        controller.initialize(account, emailService, null);
        stage.showAndWait();
    }

    @FXML
    private void handleReply() {
        Parent parent = null;
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/view/SendEmailWindow.fxml"));
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setTitle("Válasz küldése");
        stage.setScene(new Scene(parent));
        stage.initModality(Modality.APPLICATION_MODAL);
        SendEmailController controller = loader.getController();
        controller.initialize(account, emailService, emailService.getReplyMessage(selectedFolderEmails.get(messageView.getSelectionModel().getSelectedIndex())));
        stage.showAndWait();
    }

    @FXML
    private void handleReplyAll() {
        Parent parent = null;
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/view/SendEmailWindow.fxml"));
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setTitle("Válasz küldése");
        stage.setScene(new Scene(parent));
        stage.initModality(Modality.APPLICATION_MODAL);
        SendEmailController controller = loader.getController();
        controller.initialize(account, emailService, emailService.getReplyToAllMessage(selectedFolderEmails.get(messageView.getSelectionModel().getSelectedIndex())));
        stage.showAndWait();
    }

    @FXML
    private void handleForward() {
        Parent parent = null;
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/view/SendEmailWindow.fxml"));
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setTitle("Válasz küldése");
        stage.setScene(new Scene(parent));
        stage.initModality(Modality.APPLICATION_MODAL);
        SendEmailController controller = loader.getController();
        controller.initialize(account, emailService, emailService.getForwardMessage(selectedFolderEmails.get(messageView.getSelectionModel().getSelectedIndex())));
        stage.showAndWait();
    }

    @FXML
    private void handleMoveEmail() {
        if (selectedFolder == folderService.getSentFolder()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hiba");
            alert.setHeaderText("Ebből a mappából nem lehet áthelyezni leveleket!");
            alert.showAndWait();
        }
        else {
            Parent parent = null;
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/view/FolderSelectWindow.fxml"));
            try {
                parent = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage stage = new Stage();
            stage.setTitle("Mappa kiválasztása");
            stage.setScene(new Scene(parent));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            FolderSelectController controller = loader.getController();
            controller.initialize(account);
            stage.showAndWait();
            if (controller.getSelectedFolder() != null) {
                emailService.moveEmail(selectedFolderEmails.get(messageView.getSelectionModel().getSelectedIndex()), selectedFolder, folderService.getFolderByName(controller.getSelectedFolder()));
                selectedFolderEmails = selectedFolder.getMessages().stream().sorted(Comparator.comparing(EmailMessage::getTime).reversed()).collect(Collectors.toList());;
                setMessageViewItems();
            }
        }
    }

    @FXML
    private void handleDeleteEmail() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Megerősítés");
        alert.setHeaderText("Biztosan törli a kijelölt levelet?");
        alert.showAndWait().ifPresent(x -> {
            emailService.deleteEmail(selectedFolderEmails.get(messageView.getSelectionModel().getSelectedIndex()), selectedFolder);
            selectedFolderEmails = selectedFolder.getMessages().stream().sorted(Comparator.comparing(EmailMessage::getTime).reversed()).collect(Collectors.toList());;
            setMessageViewItems();
        });
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
        if (controller.getSelectedEmail() != null) {
            if (!controller.getSelectedEmail().equals(account.getEmailAddress())) {
                account = MainApp.ACCOUNT_SERVICE.getAccountByEmail(controller.getSelectedEmail());
                initialize(account);
            }
        }
    }

    @FXML
    private void handleNewFolder() {
        Parent parent = null;
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/view/FolderNameWindow.fxml"));
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
        FolderNameController controller = loader.getController();
        stage.showAndWait();
        if (controller.getFolderName() != null) {
            folderService.addFolder(new MailboxFolder(controller.getFolderName(), account));
            folderView.setItems(account.getFolders().stream().map(MailboxFolder::getName).collect(Collectors.toCollection(FXCollections::observableArrayList)));
        }
    }

    @FXML
    private void handleModifyFolder() {
        if (selectedFolder == folderService.getInboxFolder() || selectedFolder == folderService.getSentFolder() || selectedFolder == folderService.getDeletedFolder()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hiba");
            alert.setHeaderText("A kijelölt mappa rendszermappa, amely nem átnevezhető!");
            alert.showAndWait();
        }
        else {
            Parent parent = null;
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/view/FolderNameWindow.fxml"));
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
            FolderNameController controller = loader.getController();
            controller.initialize(folderView.getSelectionModel().getSelectedItem());
            stage.showAndWait();
            if (controller.getFolderName() != null) {
                selectedFolder.setName(controller.getFolderName());
                folderService.modifyFolder(selectedFolder);
                folderView.setItems(account.getFolders().stream().map(MailboxFolder::getName).collect(Collectors.toCollection(FXCollections::observableArrayList)));
            }
        }
    }

    @FXML
    private void handleDeleteFolder() {
        if (selectedFolder == folderService.getInboxFolder() || selectedFolder == folderService.getSentFolder() || selectedFolder == folderService.getDeletedFolder()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hiba");
            alert.setHeaderText("A kijelölt mappa rendszermappa, amely nem törölhető!");
            alert.showAndWait();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Megerősítés");
            alert.setHeaderText("Biztosan törli a kijelölt mappát?");
            alert.setContentText("Ezzel a művelettel a mappában lévő összes levél is törlődik.");
            alert.showAndWait().ifPresent(x -> {
                folderService.deleteFolder(selectedFolder);
                folderView.setItems(account.getFolders().stream().map(MailboxFolder::getName).collect(Collectors.toCollection(FXCollections::observableArrayList)));
                folderView.getSelectionModel().select(0);
            });
        }
    }
}
