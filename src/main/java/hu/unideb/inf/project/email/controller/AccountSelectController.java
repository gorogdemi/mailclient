package hu.unideb.inf.project.email.controller;

import hu.unideb.inf.project.email.app.MainApp;
import hu.unideb.inf.project.email.model.Account;
import hu.unideb.inf.project.email.service.AccountService;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.stream.Collectors;

public class AccountSelectController {

    private static int InstanceCounter = 0;

    @FXML
    private ListView<String> emailList;

    @FXML
    private Button selectButton;

    @FXML
    private Button modifyButton;

    @FXML
    private Button deleteButton;

    @FXML
    private void initialize() {
        InstanceCounter++;
        emailList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (emailList.getSelectionModel().getSelectedItem() != null) {
                selectButton.setDisable(false);
                modifyButton.setDisable(false);
                deleteButton.setDisable(false);
            }
            else {
                selectButton.setDisable(true);
                modifyButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });
        emailList.setItems(MainApp.ACCOUNT_SERVICE.getAccounts().stream().map(Account::getEmailAddress).collect(Collectors.toCollection(FXCollections::observableArrayList)));
    }

    @FXML
    private void handleNewAccount(){
        Parent parent = null;
        AccountSettingsController controller = new AccountSettingsController();
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/view/AccountSettingsWindow.fxml"));
        loader.setController(controller);
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setTitle("Fiókadatok");
        stage.setScene(new Scene(parent));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.showAndWait();
        emailList.setItems(MainApp.ACCOUNT_SERVICE.getAccounts().stream().map(Account::getEmailAddress).collect(Collectors.toCollection(FXCollections::observableArrayList)));
    }

    @FXML
    private void handleModify() {
        Parent parent = null;
        AccountSettingsController controller = new AccountSettingsController(MainApp.ACCOUNT_SERVICE.getAccountByEmail(emailList.getSelectionModel().getSelectedItem()));
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/view/AccountSettingsWindow.fxml"));
        loader.setController(controller);
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setTitle("Fiókadatok");
        stage.setScene(new Scene(parent));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.showAndWait();
        emailList.setItems(MainApp.ACCOUNT_SERVICE.getAccounts().stream().map(Account::getEmailAddress).collect(Collectors.toCollection(FXCollections::observableArrayList)));
    }

    @FXML
    private void handleDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Megerősítés");
        alert.setHeaderText("Biztosan törli a kijelölt e-mail fiókot?");
        alert.showAndWait().ifPresent(x -> {
            if (x == ButtonType.OK) {
                MainApp.ACCOUNT_SERVICE.deleteAccount(MainApp.ACCOUNT_SERVICE.getAccountByEmail(emailList.getSelectionModel().getSelectedItem()));
                emailList.setItems(MainApp.ACCOUNT_SERVICE.getAccounts().stream().map(Account::getEmailAddress).collect(Collectors.toCollection(FXCollections::observableArrayList)));
            }
        });
    }

    @FXML
    private void handleCancel() {
        if (InstanceCounter == 1) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Megerősítés");
            alert.setHeaderText("Biztosan nem választ fiókot?");
            alert.setContentText("Nincs kiválasztva fiók, ezért az OK gombra kattintva a program bezár. Kattintson a Mégsem gombra, ha mégis fiókot szeretne választani.");
            alert.showAndWait().ifPresent(x -> {
                if (x == ButtonType.OK) {
                    System.exit(0);
                }
            });
        }
        else {
            Stage stage = (Stage) emailList.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void handleSelectAndClose() {
        if (emailList.getSelectionModel().getSelectedItem() != null) {
            Stage stage = (Stage) emailList.getScene().getWindow();
            stage.close();
        }
    }

    public String getSelectedEmail() {
        return emailList.getSelectionModel().getSelectedItem();
    }
}
