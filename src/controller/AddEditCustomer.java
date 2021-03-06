package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import main.Main;

import database.DBConnection;
import database.DBInteraction;

import model.Customer;
import utilities.InputValidator;


public class AddEditCustomer {

    @FXML private TextField customer_id, customer_name, customer_address, customer_postal_code, customer_phone;
    @FXML private Label customer_label;
    @FXML private ComboBox customer_country, customer_division;

    /**
     * Grabs all the data for a selected customer, and inserts that data into their respective elements
     * to allow the user to edit them easily.
     */
    public void initialize() {

        /*
          If editing a customer that already exists, get the data for that
          customer and insert the data into the correct scene elements
         */
        try {
            if (Main.updateDatabase) {
                customer_division.setDisable(false);
                customer_label.setText("Edit Customer");

                /* Insert data into form */
                customer_id.setText(String.valueOf(Main.selectedCustomer.getCustomer_id()));
                customer_name.setText(Main.selectedCustomer.getCustomer_name());
                customer_address.setText(Main.selectedCustomer.getAddress());
                customer_postal_code.setText(Main.selectedCustomer.getPostal_code());
                customer_phone.setText(Main.selectedCustomer.getPhone());
                customer_division.setValue(Main.selectedCustomer.getDivision());

                /* Perform an SQL query based upon the Customer ID */
                String string = """
                        SELECT *
                        FROM   customers
                        WHERE  customer_id = '%s'"""
                        .formatted(Main.selectedCustomer.getCustomer_id());

                PreparedStatement ps = DBConnection.getConnection().prepareStatement(string);
                ResultSet result = ps.executeQuery();
                while (result.next()) {

                    /* Handle data insertion of country and division comboboxes */
                    for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
                        String columnName = result.getMetaData().getColumnName(i);
                        if (columnName.equals("Division_ID")) {
//
                            /* Get country ID */
                            String query = "SELECT Country_ID FROM first_level_divisions WHERE division_id = '%s'"
                                    .formatted(result.getString(i));

                            String countryID = DBInteraction.simpleQuery(query);

//                            /** Get country name from its ID and set combobox to that value */
                            query = "SELECT Country FROM countries WHERE country_id = '%s'"
                                    .formatted(countryID);
                            DBInteraction.setSelectedComboBoxOption(query, customer_country);



                            selectCustomerCountry();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* Add countries to their combobox. Divisions will be added to the scene later. */
        DBInteraction.getComboBoxOptions("SELECT country FROM countries", customer_country);
    }

    /**
     * Cancel add/edit of a customer and return to index
     * @param event The event that triggered this method
     * @throws Exception
     */
    public void cancelCustomer(ActionEvent event) throws Exception {
        SceneController.changeScene("/view/Index.fxml", "Scheduler", event, false);
    }

    /**
     * Validates and saves data to the SQL database
     * @param event The event that triggered this method
     * @throws Exception
     */
    public void saveCustomer(ActionEvent event) throws Exception {

        boolean textFieldsFilled =  InputValidator.textFieldFilled(customer_name, "a name") &&
                                    InputValidator.textFieldFilled(customer_address, "an address") &&
                                    InputValidator.textFieldFilled(customer_postal_code, "a postal code") &&
                                    InputValidator.textFieldFilled(customer_phone, "a phone number");

        boolean comboBoxesFilled =  InputValidator.comboBoxSelected(customer_country, "a country") &&
                                    InputValidator.comboBoxSelected(customer_division, "a division");

        boolean isValidPhoneNum  =  InputValidator.isPhoneNumber(customer_phone.getText());

        if (textFieldsFilled && comboBoxesFilled && isValidPhoneNum) {

            /* Get strings from input fields */
            Customer customer = new Customer();
            if (!customer_id.getText().equals("")) {
                customer.setCustomer_id(Integer.parseInt(customer_id.getText()));
            }
            customer.setCustomer_name(customer_name.getText());
            customer.setAddress(customer_address.getText());
            customer.setPostal_code(customer_postal_code.getText());
            customer.setPhone(customer_phone.getText());
            customer.setDivision(customer_division.getValue().toString());


            /* Convert division name to its respective id and store it in the model*/
            String getDivisionIDQuery = "SELECT division_id from first_level_divisions WHERE Division = '" + customer.getDivision() + "'";
            String customerDivisionID = String.valueOf(DBInteraction.simpleQuery(getDivisionIDQuery));
            customer.setDivision(customerDivisionID);
            /*
              Check if there's a customer id already
              If there is one already, update a row that already exists
              If not, insert a new row into the SQL database
             */
            String query;
            if (Main.updateDatabase) {
                query = """
                        UPDATE customers
                        SET    customer_name = '%s',
                               address = '%s',
                               postal_code = '%s',
                               phone = '%s',
                               division_id = '%s'
                        WHERE  customer_id = '%s'"""
                        .formatted(
                                customer.getCustomer_name(),
                                customer.getAddress(),
                                customer.getPostal_code(),
                                customer.getPhone(),
                                customer.getDivision(),
                                customer.getCustomer_id()
                        );
            }
            else {
                query = """
                        INSERT INTO customers
                                    (customer_name,
                                     address,
                                     postal_code,
                                     phone,
                                     division_id)
                        VALUES      ('%s',
                                     '%s',
                                     '%s',
                                     '%s',
                                     '%s')"""
                        .formatted(
                                customer.getCustomer_name(),
                                customer.getAddress(),
                                customer.getPostal_code(),
                                customer.getPhone(),
                                customer.getDivision()
                        );
            }
            DBInteraction.update(query);

            SceneController.changeScene("/view/Index.fxml", "Scheduler", event, false);
        }
    }

    /**
     * Changes available options in the division combobox based on which country is selected
     * Called when editing an already existing customer, or when a user selects a customer
     */
    public void selectCustomerCountry() {
        customer_division.setDisable(false);
        String selectedCountry = customer_country.getValue().toString();

        String query = """
                SELECT division
                FROM   first_level_divisions
                       INNER JOIN countries
                               ON countries.country_id = first_level_divisions.country_id
                WHERE  country = '%s'""".formatted(selectedCountry);
        DBInteraction.getComboBoxOptions(query, customer_division);
    }
}
