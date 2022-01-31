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

    /**
     * Checks if user wants to edit or add a contact, and goes from there
     * @throws SQLException
     */
    public void initialize() {

        /**
         * If editing a contact that already exists, get the data for that
         * contact and insert the data into the correct scene elements
         */
        final String[][] array = {null};
        try {
            if (Main.updateDatabase) {

                contact_label.setText("Edit Contact");
                /** Get data of a selected row in a TableView and format it*/
                Main.selectedContact.forEach((contact) -> {
                    String selectedRow = RemoveSquareBrackets.go(contact.toString());
                    array[0] = selectedRow.split(",");
                });

                /** Perform an SQL query based upon the Contact ID */
                String string = "SELECT * FROM contacts WHERE contact_id = '" + array[0][0] + "'";
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(string);
                ResultSet result = ps.executeQuery();

                while (result.next()) {

                    /** Insert data into textfields */
                    contact_id.setText(result.getString("Contact_ID"));
                    contact_name.setText(result.getString("Contact_Name"));
                    contact_email.setText(result.getString("Email"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    /**
     * Cancel add/edit of a contact and return to index
     * @param event The event that triggered this method
     * @throws Exception
     */
    public void cancelContact(ActionEvent event) throws Exception {
        SceneController.changeScene("/view/Index.fxml", "Scheduler", event, false);
    }

    /**
     * Validates and saves data to the SQL database
     * @param event The event that triggered this method
     * @throws Exception
     */
    public void saveContact(ActionEvent event) throws Exception {
        if (InputValidator.textFieldFilled(contact_name, "a name") &&
            InputValidator.textFieldFilled(contact_email, "an email") &&
            InputValidator.isEmail(contact_email.getText())) {

            /** Get strings from input fields */
            String contactID = contact_id.getText();
            String contactName = contact_name.getText();
            String contactEmail = contact_email.getText();

            /**
             * Check if there's a contact id already
             * If there is one already, update a row that already exists
             * If not, insert a new row into the SQL database
             */
            String query;
            if (contactID.equals("")) {
                query = "INSERT INTO contacts (Contact_Name, Email) " +
                        "VALUES ('" + contactName + "', '" + contactEmail + "')";

            }
            else {
                query = "UPDATE contacts SET Contact_Name = '" + contactName + "', Email = '" + contactEmail + "'" +
                        "WHERE Contact_ID = '" + contactID + "'";
            }
            DBInteraction.update(query);

            SceneController.changeScene("/view/Index.fxml", "Scheduler", event, false);
        }
    }
}
