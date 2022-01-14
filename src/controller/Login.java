package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utilities.Popup;

import java.net.URL;
import java.time.ZoneId;
import java.util.ResourceBundle;

import static model.Query.auth;

public class Login implements Initializable {

    @FXML private TextField login_username;
    @FXML private PasswordField login_password;
    @FXML private Label login_error, zone_id;
    @FXML private Button login_button;
    @FXML private VBox login_vbox;

    private boolean toggle = false;

    ResourceBundle resources = ResourceBundle.getBundle("Localization");

    public void setZoneText() {
        ZoneId zone = ZoneId.systemDefault();
        String string = zone.getId();
        zone_id.setText(resources.getString("time_zone") + " " + string);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setZoneText();
    }

    public void onLoginButtonPressed(ActionEvent actionEvent) throws Exception {
        String username = login_username.getText().toLowerCase();
        String password = login_password.getText();
        boolean auth = auth("SELECT User_ID, User_Name, Password FROM users WHERE User_Name = '" + username + "' AND Password = '" + password + "'");
        if (auth == true) {
            Parent root = FXMLLoader.load(getClass().getResource("/view/Index.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            root.requestFocus();
        }
        else {
            Popup.errorAlert("error", "login_error");
        }
    }
}
