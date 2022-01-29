package main;

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
    public static ObservableList selectedAppointment = null;
    public static ObservableList selectedCustomer    = null;
    public static ObservableList selectedContact     = null;
    public static boolean updateDatabase             = false;
    public static String username;
    public static Integer userID;

    /** Debug booleans */
    public static boolean franceToggle     = false;
    static boolean resetDatabaseToDefaults = true;


    /**
     * Creates stage, applies scene to it, and shows the stage.
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
        root.requestFocus();

        if (resetDatabaseToDefaults == true) {
            boolean result = Popup.confirmationAlert("database_reset", "reset_confirm");
            if (result) {
                ResetDatabase.toDefaults();
            }
        }
    }

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
        launch(args);
        DBConnection.closeConnection();
    }
}
