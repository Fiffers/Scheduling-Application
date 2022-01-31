package main;

import controller.SceneController;
import database.DBConnection;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ResourceBundle;
import java.util.Locale;

import utilities.Popup;
import utilities.ResetDatabase;

public class Main extends Application {
    /** Just some global variables, nothing to see here =) */
    public static ObservableList selectedAppointment;
    public static ObservableList selectedCustomer;
    public static ObservableList selectedContact;
    public static String username;
    public static Integer userID;
    public static boolean updateDatabase   = false;

    /** Debug booleans */
    static boolean franceToggle            = false;
    static boolean resetDatabaseToDefaults = true;

    /**
     * Creates stage, applies scene to it, and shows the stage.
     * @param stage The stage for the application
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {

        ResourceBundle resources = getResources();
        Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"), resources);

        stage.setTitle(resources.getString("login"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/resources/bootstrap.css");
        stage.setScene(scene);
        stage.show();
        SceneController.centerStage(stage);
        root.requestFocus();

        if (resetDatabaseToDefaults == true) {
            boolean result = Popup.confirmationAlert("database_reset", "reset_confirm");
            if (result) {
                ResetDatabase.toDefaults();
            }
        }
    }

    /**
     * Gets localization stuff
     * @return resource bundle
     */
    public static ResourceBundle getResources() {
        Locale l;
        if (franceToggle) {
            String lang = "fr";
            String country = "FR";
            l = new Locale(lang, country);
        }
        else {
            l = Locale.getDefault();
        }

        ResourceBundle resources = ResourceBundle.getBundle("Localization", l);
        return resources;
    }

    /**
     * Main function where the program begins.
     * @param args Unused
     */
    public static void main(String[] args) throws Exception {
        DBConnection.startConnection();
        launch();
        DBConnection.closeConnection();
    }
}
