package controller;

import database.DBConnection;
import database.DBInteraction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import main.Main;
import utilities.InputValidator;
import utilities.RemoveSquareBrackets;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddEditContact {
    @FXML private TextField contact_id, contact_name, contact_email;
    @FXML private Label contact_label;
    public void initialize() {
        final String[][] array = {null};
        if (Main.updateDatabase) {
            contact_label.setText("Edit Contact");
            try {
                Main.selectedContact.forEach((contact) -> {
                    String selectedRow = RemoveSquareBrackets.go(contact.toString());
                    array[0] = selectedRow.split(",");
                });
                String string = "SELECT * FROM contacts WHERE contact_id = '" + array[0][0] + "'";
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(string);
                ResultSet result = ps.executeQuery();

                while (result.next()) {
                    contact_id.setText(result.getString("Contact_ID"));
                    contact_name.setText(result.getString("Contact_Name"));
                    contact_email.setText(result.getString("Email"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public void cancelContact(ActionEvent event) throws Exception {
        SceneController.changeScene("/view/Index.fxml", "Scheduler", event, false);
    }
    public void saveContact(ActionEvent event) throws Exception {
        if (InputValidator.textFieldFilled(contact_name, "a name") &&
            InputValidator.textFieldFilled(contact_email, "an email") &&
            InputValidator.isEmail(contact_email.getText())) {

            String contactID = contact_id.getText();
            String contactName = contact_name.getText();
            String contactEmail = contact_email.getText();

            if (!contactID.equals("")) {
                String query = "UPDATE contacts SET Contact_Name = '" + contactName + "', Email = '" + contactEmail + "'" +
                        "WHERE Contact_ID = '" + contactID + "'";
                DBInteraction.update(query);
            }
            else {
                String query = "INSERT INTO contacts (Contact_Name, Email) " +
                        "VALUES ('" + contactName + "', '" + contactEmail + "')";
                System.out.println(query);
                DBInteraction.update(query);
            }
            SceneController.changeScene("/view/Index.fxml", "Scheduler", event, false);
        }
    }
}
