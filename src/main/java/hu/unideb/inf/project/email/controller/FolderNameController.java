package hu.unideb.inf.project.email.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FolderNameController {

    @FXML
    private TextField nameField;

    public void initialize(String folderName) {
        nameField.setText(folderName);
    }

    @FXML
    private void handleOK() {
        if (nameField.getText() != null) {
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.close();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hiba");
            alert.setHeaderText("A mappanév mező üres!");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleCancel() {
        nameField.setText("");
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    public String getFolderName() {
        return nameField.getText().equals("") ? null : nameField.getText();
    }
}
