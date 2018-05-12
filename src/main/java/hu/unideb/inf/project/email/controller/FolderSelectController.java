package hu.unideb.inf.project.email.controller;

import hu.unideb.inf.project.email.model.Account;
import hu.unideb.inf.project.email.model.MailboxFolder;
import hu.unideb.inf.project.email.service.FolderService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.stream.Collectors;

public class FolderSelectController {

    @FXML
    private ListView<String> folderView;

    @FXML
    private Button okButton;

    public void initialize(Account account) {
        FolderService folderService = new FolderService(account);
        folderView.setItems(account.getFolders().stream().filter(x -> x != folderService.getSentFolder() && x != folderService.getDeletedFolder())
                .map(MailboxFolder::getName).collect(Collectors.toCollection(FXCollections::observableArrayList)));
    }

    @FXML
    private void initalize() {
        folderView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> okButton.setDisable(folderView.getSelectionModel().getSelectedItem() == null));
    }

    @FXML
    private void handleOK() {
        if (folderView.getSelectionModel().getSelectedItem() != null) {
            Stage stage = (Stage) folderView.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) folderView.getScene().getWindow();
        stage.close();
    }

    public String getSelectedFolder() {
        return folderView.getSelectionModel().getSelectedItem();
    }
}
