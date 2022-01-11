package utilities;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

import java.util.Locale;
import java.util.ResourceBundle;

public class Alert {
    public static void okayAlert(String title, String buttonText, String message) {
        ResourceBundle resources = ResourceBundle.getBundle("Localization");
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(resources.getString(title));

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                Alert.class.getResource("/resources/bootstrap.css").toExternalForm()
        );
        ButtonType buttonType = new ButtonType(resources.getString(buttonText));
        alert.getButtonTypes().setAll(buttonType);
        Node button = alert.getDialogPane().lookupButton(buttonType);
        alert.setContentText(resources.getString(message));
        button.getStyleClass().add("primary");
        alert.showAndWait();
    }
}
