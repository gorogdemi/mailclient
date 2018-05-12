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

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MainController {

    private Account account;
    private EmailService emailService;
    private FolderService folderService;
    private MailboxFolder selectedFolder;

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
            setMessageViewItems(emailService.searchEmails(selectedFolder.getMessagesWithoutDeleted(),
                    searchField.getText().toLowerCase(), getBoolFromReadState(newValue)));
        });
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            setMessageViewItems(emailService.searchEmails(selectedFolder.getMessagesWithoutDeleted(), newValue.toLowerCase(), getBoolFromReadState(filterBox.getSelectionModel().getSelectedItem())));
        });
        folderView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (folderView.getSelectionModel().getSelectedItem() != null) {
                modifyFolderButton.setDisable(false);
                deleteFolderButton.setDisable(false);
                selectedFolder = folderService.getFolder(folderView.getSelectionModel().getSelectedItem());
                setMessageViewItems(emailService.getEmailsFromFolder(selectedFolder));
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
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
                EmailMessage message = parseFromView(messageView.getSelectionModel().getSelectedItem());
                String email = null;
                try {
                    InternetAddress address = new InternetAddress(message.getSender());
                    String personal = address.getPersonal();
                    if (personal != null) {
                        address.setPersonal(personal, "utf-8");
                    }
                    if (address.getPersonal() != null)
                        email = address.getPersonal() + " - " + address.getAddress();
                } catch (AddressException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                sb.append("Feladó: ").append(email).append('\n');
                sb.append("Címzett: ").append(message.getRecipients()).append('\n');
                if (message.getCc() != null)
                    sb.append("Másolatot kap: ").append(message.getCc()).append('\n');
                sb.append("Beérkezés ideje: ").append(message.getTime().format(formatter)).append('\n');
                sb.append("Tárgy: ").append(message.getSubject()).append("\n\n");
                sb.append(message.getBody());
                emailView.setText(sb.toString());
                if (!message.isRead()) {
                    message.setRead(true);
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

    private EmailMessage parseFromView(String row) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        String[] splitted = row.split("\n");
        int index = splitted[1].lastIndexOf(" - ");
        String subject = splitted[1].substring(0, index);
        String date = splitted[1].substring(index + 3);
        return selectedFolder.getMessagesWithoutDeleted().stream().filter(x -> x.getSubject().equals(subject) && x.getTime().format(formatter).equals(date)).collect(Collectors.toList()).get(0);
    }

    private void setMessageViewItems(List<EmailMessage> messageList) {
        messageView.setItems(messageList.stream().sorted(Comparator.comparing(EmailMessage::getTime).reversed())
                .map(x -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
                    try {
                        InternetAddress address = new InternetAddress(x.getSender());
                        String personal = address.getPersonal();
                        if (personal != null) {
                            address.setPersonal(personal, "utf-8");
                            return String.format("%s\n%s - %s", !x.isRead() ? "[ÚJ] " + personal : personal, x.getSubject(), x.getTime().format(formatter));
                        }
                        return String.format("%s - %s\n%s", !x.isRead() ? "[ÚJ] " + address.getAddress() : address.getAddress(), x.getSubject(), x.getTime().format(formatter));
                    } catch (AddressException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toCollection(FXCollections::observableArrayList)));
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
        controller.initialize(account, emailService, emailService.getReplyMessage(parseFromView(messageView.getSelectionModel().getSelectedItem())));
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
        controller.initialize(account, emailService, emailService.getReplyToAllMessage(parseFromView(messageView.getSelectionModel().getSelectedItem())));
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
        controller.initialize(account, emailService, emailService.getForwardMessage(parseFromView(messageView.getSelectionModel().getSelectedItem())));
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
                folderService.moveMessage(parseFromView(messageView.getSelectionModel().getSelectedItem()), selectedFolder, folderService.getFolder(controller.getSelectedFolder()));
                setMessageViewItems(emailService.getEmailsFromFolder(selectedFolder));
            }
        }
    }

    @FXML
    private void handleDeleteEmail() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Megerősítés");
        alert.setHeaderText("Biztosan törli a kijelölt levelet?");
        alert.showAndWait().ifPresent(x -> {
            emailService.deleteEmail(parseFromView(messageView.getSelectionModel().getSelectedItem()), selectedFolder);
            setMessageViewItems(emailService.getEmailsFromFolder(selectedFolder));
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
        if (controller.getSelectedEmail() != null)
            account = MainApp.ACCOUNT_SERVICE.getAccountByEmail(controller.getSelectedEmail());
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
            folderService.addFolder(controller.getFolderName());
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
            alert.showAndWait().ifPresent(x -> {
                folderService.deleteFolder(selectedFolder);
                folderView.setItems(account.getFolders().stream().map(MailboxFolder::getName).collect(Collectors.toCollection(FXCollections::observableArrayList)));
            });
        }
    }
}
