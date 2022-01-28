package utilities;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Alert;
import main.Main;

import java.util.Optional;
import java.util.ResourceBundle;

import static utilities.Print.print;

/**
 * This class handles all alert popups the application needs.
 */
public class Popup {
    static ResourceBundle resources = Main.getResources();
    /**
     * Builds out the basic bits of the alert pane
     * @param title The title for the pane
     * @param message The message displayed in the pane
     * @param alert The alert object
     * @return The alert object
     */
    public static Alert buildAlert (String title, String message, Alert alert) {

        alert.setTitle(title);
        alert.setContentText(message);

        try {
            alert.setTitle(resources.getString(title));
            alert.setContentText(resources.getString(message));
        } catch (Exception e) {
            // ignore cause I truly do not care
        }

        alert.getButtonTypes().clear();

        /** Add custom CSS to the pane */
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Popup.class.getResource("/resources/bootstrap.css").toExternalForm());
        return alert;
    }

    /**
     * Builds the buttons for the alert pane
     * @param buttonCount Number of buttons to add to the pane. Must == buttonText.length!
     * @param buttonText Array of strings containing text to go on buttons
     * @param alert The alert object
     * @return The alert object
     */
    public static Alert buildButtons (int buttonCount, String[] buttonText, Alert alert){
        try {
            for (int i = 0; i < buttonCount; i++) {
                ButtonType buttonType = new ButtonType(buttonText[i]);
                try {
                    buttonType = new ButtonType(resources.getString(buttonText[i]));
                } catch (Exception e) {
                    // Dont care
                }


                alert.getButtonTypes().add(buttonType);
                Node button = alert.getDialogPane().lookupButton(buttonType);
                button.getStyleClass().add("primary");
            }

        } catch (Exception e) {
            print("ERROR: buttonCount != buttonText.length\nEXCEPTION: " + e);
        }
        return alert;
    }

    /**
     * Creates an "ERROR" type alert
     * @param title The title for the alert
     * @param message The message for the alert
     */
    public static void errorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert = buildAlert(title, message, alert);
        alert = buildButtons(1, new String[]{"ok"}, alert);
        alert.showAndWait();
    }

    /**
     * Creates a "CONFIRMATION" type alert
     * @param title The title for the alert
     * @param message The message for the alert
     * @return user button selection
     */
    public static boolean confirmationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert = buildAlert(title, message, alert);
        alert = buildButtons(2, new String[]{"yes", "no"}, alert);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get().getText().equals(resources.getString("yes"))) {
            return true;
        }
        else {
            return false;
        }
    }
}
