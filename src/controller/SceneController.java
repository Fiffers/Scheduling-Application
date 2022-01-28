package controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.Main;

import java.io.IOException;

public class SceneController {
    public static void changeScene(String location, String title, Event event, boolean toggle) throws IOException {
        Main.updateDatabase = toggle;
        Parent root = FXMLLoader.load(SceneController.class.getResource(location));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
        root.requestFocus();
    }
}
