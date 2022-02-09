package controller;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import main.Main;

import java.io.IOException;
import java.util.Objects;
import java.util.ResourceBundle;

public class SceneController {
    /**
     * Changes the scene
     * @param location The path to the scene
     * @param title The title of the scene
     * @param event The event that triggered the scene change
     * @param toggle Whether the user wants to update an already existing row in an SQL table
     */
    public static void changeScene(String location, String title, Event event, boolean toggle) throws IOException {
        ResourceBundle resourceBundle = Main.getResources();
        Main.updateDatabase = toggle;
        Parent root = FXMLLoader.load(Objects.requireNonNull(SceneController.class.getResource(location)), resourceBundle);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
        centerStage(stage);
        root.requestFocus();
    }

    /**
     * Centers the stage on your monitor. Kinda buggy, but it works.
     * @param stage The stage object to center
     */
    public static void centerStage(Stage stage) {
        double screenX = Screen.getPrimary().getVisualBounds().getWidth();
        double screenY = Screen.getPrimary().getVisualBounds().getHeight();

        stage.setX((screenX - stage.getWidth()) / 2);
        stage.setY((screenY - stage.getHeight()) / 2);
    }
}
