package hu.unideb.inf.project.email.app;

import hu.unideb.inf.project.email.controller.AccountSelectController;
import hu.unideb.inf.project.email.controller.MainController;
import hu.unideb.inf.project.email.service.AccountService;
import hu.unideb.inf.project.email.utility.EntityManagerFactoryUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.IOException;

public class MainApp extends Application {

    public static final AccountService ACCOUNT_SERVICE = new AccountService();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent mainParent = null;
        FXMLLoader mainLoader = new FXMLLoader(MainApp.class.getResource("/view/MainWindow.fxml"));
        try {
            mainParent = mainLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.setTitle("Email kliens");
        primaryStage.setScene(new Scene(mainParent, 1000, 600));
        primaryStage.setOnShown(event -> {
            if (ACCOUNT_SERVICE.getAccountCount() == 1) {
                MainController mainController = mainLoader.getController();
                mainController.initialize(ACCOUNT_SERVICE.getAccounts().get(0));
            }
            else {
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
                MainController mainController = mainLoader.getController();
                mainController.initialize(ACCOUNT_SERVICE.getAccountByEmail(controller.getSelectedEmail()));
            }
        });
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        EntityManagerFactoryUtil.getInstance().close();
    }
}
