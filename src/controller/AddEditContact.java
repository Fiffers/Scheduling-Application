package controller;


import database.DBInteraction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import main.Main;
import model.Contact;
import utilities.InputValidator;

public class AddEditContact {

    @FXML private TextField contact_id, contact_name, contact_email;
    @FXML private Label contact_label;

    /**
     * Grabs all the data for a selected contact, and inserts that data into their respective elements
     * to allow the user to edit them easily.
     */
    public void initialize() {

        /*
          If editing a contact that already exists, get the data for that
          contact and insert the data into the correct scene elements
         */
        try {
            if (Main.updateDatabase) {
                contact_label.setText("Edit Contact");
                contact_id.setText(String.valueOf(Main.selectedContact.getContact_id()));
                contact_name.setText(Main.selectedContact.getContact_name());
                contact_email.setText(Main.selectedContact.getEmail());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * Cancel add/edit of a contact and return to index
     * @param event The event that triggered this method
     */
    public void cancelContact(ActionEvent event) throws Exception {
        SceneController.changeScene("/view/Index.fxml", "Scheduler", event, false);
    }

    /**
     * Validates and saves data to the SQL database
     * @param event The event that triggered this method
     */
    public void saveContact(ActionEvent event) throws Exception {
        if (InputValidator.textFieldFilled(contact_name, "a name") &&
            InputValidator.textFieldFilled(contact_email, "an email") &&
            InputValidator.isEmail(contact_email.getText())) {

            /* Get strings from input fields */
            Contact contact = new Contact();
            contact.setContact_id(Integer.parseInt(contact_id.getText()));
            contact.setContact_name(contact_name.getText());
            contact.setEmail(contact_email.getText());

            /*
              Check if we want to update the database
              If true, update a row that already exists
              If not, insert a new row into the SQL database
             */
            String query;
            if (Main.updateDatabase) {
                query = "UPDATE contacts SET Contact_Name = '" + contact.getContact_name() + "', Email = '" + contact.getEmail() + "'" +
                        "WHERE Contact_ID = '" + contact.getContact_id() + "'";
            }
            else {
                query = "INSERT INTO contacts (Contact_Name, Email) " +
                        "VALUES ('" + contact.getContact_name() + "', '" + contact.getEmail() + "')";
            }
            DBInteraction.update(query);

            SceneController.changeScene("/view/Index.fxml", "Scheduler", event, false);
        }
    }
}
