package main;

import controller.SceneController;
import database.DBConnection;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Locale;

import model.Appointment;
import model.Contact;
import model.Customer;
import utilities.Popup;
import utilities.ResetDatabase;

public class Main extends Application {
    /* Some globals */
    public static Appointment selectedAppointment;
    public static Customer selectedCustomer;
    public static Contact selectedContact;
    public static String username;
    public static Integer userID;
    public static boolean updateDatabase   = false;

    /* Debug booleans */
    static boolean franceToggle            = false;
    static boolean resetDatabaseToDefaults = false;

    /**
     * Creates stage, applies scene to it, and shows the stage.
     * @param stage The stage for the application
     */
    @Override
    public void start(Stage stage) throws Exception {

        ResourceBundle resources = getResources();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/Login.fxml")), resources);

        stage.setTitle(resources.getString("login"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/resources/bootstrap.css");
        stage.setScene(scene);
        stage.show();
        SceneController.centerStage(stage);
        root.requestFocus();

        if (resetDatabaseToDefaults) {
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

        return ResourceBundle.getBundle("Localization", l);
    }

    /**
     * Main function where the program begins.
     * @param args Unused
     */
    public static void main(String[] args) {
        DBConnection.startConnection();
        launch();
        DBConnection.closeConnection();
    }


}
