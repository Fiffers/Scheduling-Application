package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.net.URL;
import java.time.ZoneId;
import java.util.ResourceBundle;

import static model.Query.auth;

import javafx.stage.Stage;
import main.Main;

public class Login implements Initializable {

    @FXML private TextField login_username;
    @FXML private PasswordField login_password;
    @FXML private Label login_error, zone_id;

    public void setZoneText() {
        ZoneId zone = ZoneId.systemDefault();
        String string = zone.getId();
        zone_id.setText("Time Zone: " + string);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setZoneText();
    }

    public void onLoginButtonPressed(ActionEvent actionEvent) throws Exception {
        String username = login_username.getText();
        String password = login_password.getText();
        boolean auth = auth("SELECT * FROM users WHERE User_Name = '" + username + "' AND Password = '" + password + "'");
        if (auth == true) {
            Main.username = username;
            Parent root = FXMLLoader.load(getClass().getResource("/view/Index.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        }
        else {
            login_error.setVisible(true);
        }
    }
}
