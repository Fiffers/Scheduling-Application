package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.Main;
import utilities.Popup;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ResourceBundle;

import static database.DBInteraction.auth;

public class Login implements Initializable {

    @FXML private TextField login_username;
    @FXML private PasswordField login_password;
    @FXML private Label  zone_id;

    ResourceBundle resources = Main.getResources();

    /**
     * Sets the text to display which time zone the user is in.
     */
    public void setZoneText() {
        ZoneId zone = ZoneId.systemDefault();
        String string = zone.getId();
        zone_id.setText(resources.getString("time_zone") + " " + string);
    }

    /**
     * Calls setZoneText only for now
     * @param url Not used
     * @param resourceBundle Not used
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setZoneText();
    }

    /**
     * Verifies the user and displays next scene if user is successfully authenticated
     * @param actionEvent The button press action
     * @throws Exception
     */
    public void onLoginButtonPressed(ActionEvent actionEvent) throws SQLException, IOException {
        String username = login_username.getText().toLowerCase();
        String password = login_password.getText();
        boolean auth = auth("SELECT User_ID, User_Name, Password FROM users WHERE User_Name = '" + username + "' AND Password = '" + password + "'");
        try {
            if (auth == true) {
                SceneController.changeScene("/view/Index.fxml", "Scheduler", actionEvent, false);
            }
            else {
                Popup.errorAlert("error", "login_error");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
