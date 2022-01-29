
package controller;

import database.DBConnection;
import database.DBInteraction;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import main.Main;
import utilities.InputValidator;
import utilities.RemoveSquareBrackets;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddEditCustomer {
    /**
     * Requirements:
     * Customer data is displayed using a TableView, including first-level division data.
     * A list of all the customers and their information may be viewed in a TableView, and updates of the data can be performed in text fields on the form.
     */
    @FXML private TextField customer_id, customer_name, customer_address, customer_postal_code, customer_phone;
    @FXML private Label customer_label;
    @FXML private ComboBox customer_country, customer_division;

    public void initialize() throws SQLException {
        final String[][] array = {null};
        try {
            if (Main.updateDatabase) {
                customer_label.setText("Edit Customer");
                customer_division.setDisable(false);
                Main.selectedCustomer.forEach((customer) -> {
                    String selectedRow = RemoveSquareBrackets.go(customer.toString());
                    array[0] = selectedRow.split(",");
                });
                String string = "SELECT * FROM customers WHERE customer_id = '" + array[0][0] + "'";
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(string);
                ResultSet result = ps.executeQuery();
                while (result.next()) {
                    customer_id.setText(result.getString("Customer_ID"));
                    customer_name.setText(result.getString("Customer_Name"));
                    customer_address.setText(result.getString("Address"));
                    customer_postal_code.setText(result.getString("Postal_Code"));
                    customer_phone.setText(result.getString("Phone"));

                    for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
                        String columnName = result.getMetaData().getColumnName(i);
                        if (columnName.equals("Division_ID")) {
                            /** Get country ID */
                            String query = "SELECT Country_ID FROM first_level_divisions WHERE division_id = '" +
                                    result.getString(i) + "'";
                            String countryID = String.valueOf(DBInteraction.query(query));
                            countryID = RemoveSquareBrackets.go(countryID);

                            /** Get country name from its ID and set combobox to that value */
                            query = "SELECT Country FROM countries WHERE country_id = '" + countryID + "'";
                            String country = String.valueOf(DBInteraction.query(query));
                            country = RemoveSquareBrackets.go(country);
                            customer_country.setValue(country);

                            /** Get division name from Divison ID and set combobox to that value */
                            query = "SELECT Division FROM first_level_divisions WHERE division_id = '" +
                                    result.getString(i) + "'";
                            String division = String.valueOf(DBInteraction.query(query));
                            division = RemoveSquareBrackets.go(division);
                            customer_division.setValue(division);
                            selectCustomerCountry();

                        }
                        if (columnName.equals("Division_ID")) {

                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ObservableList countries = DBInteraction.query("SELECT country from countries");
        countries.replaceAll((country) -> {
            country = RemoveSquareBrackets.go(country.toString());
            return country;
        });

        customer_country.setItems(countries);



    }
    public void cancelCustomer(ActionEvent event) throws Exception {
        SceneController.changeScene("/view/Index.fxml", "Scheduler", event, false);
    }
    public void saveCustomer(ActionEvent event) throws Exception {
        if (InputValidator.textFieldFilled(customer_name, "a name") &&
            InputValidator.textFieldFilled(customer_address, "an address") &&
            InputValidator.textFieldFilled(customer_postal_code, "a postal code") &&
            InputValidator.textFieldFilled(customer_phone, "a phone number") &&
            InputValidator.comboBoxSelected(customer_country, "a country") &&
            InputValidator.comboBoxSelected(customer_division, "a division")) {

            String customerID = customer_id.getText();
            String customerName = customer_name.getText();
            String customerAddress  = customer_address.getText();
            String customerPostalCode = customer_postal_code.getText();
            String customerPhone = customer_phone.getText();
            String customerCountry = customer_country.getValue().toString();
            String customerDivision = customer_division.getValue().toString();

            String getDivisionIDQuery = "SELECT division_id from first_level_divisions WHERE Division = '" + customerDivision + "'";
            String customerDivisionID = String.valueOf(DBInteraction.query(getDivisionIDQuery));
            customerDivisionID = RemoveSquareBrackets.go(customerDivisionID);

            if (customerID.equals("")) {
                String query = "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Division_ID) " +
                        "VALUES ('" + customerName + "', '" + customerAddress + "', '" + customerPostalCode + "', '" +
                        customerPhone + "', '" + Integer.valueOf(customerDivisionID) + "')";
                DBInteraction.update(query);
            }
            else {
                String query = "UPDATE customers SET Customer_Name = '" + customerName + "', Address = '" + customerAddress + "'," +
                        " Postal_Code = '" + customerPostalCode + "', Phone = '" + customerPhone + "', Division_ID = '" +
                        Integer.valueOf(customerDivisionID) + "' " +
                        "WHERE Customer_ID = '" + customerID + "'";
                DBInteraction.update(query);
            }
            SceneController.changeScene("/view/Index.fxml", "Scheduler", event, false);
        }
//        InputValidator.comboBoxSelected(customer_country, "a country");
    }

    public void selectCustomerCountry() throws Exception {
        customer_division.setDisable(false);
        String selectedCountry = customer_country.getValue().toString();

        String query = "SELECT division FROM first_level_divisions " +
                "INNER JOIN countries ON countries.Country_ID=first_level_divisions.Country_ID " +
                "WHERE Country = '" + selectedCountry + "'";
        ObservableList divisions = DBInteraction.query(query);
        divisions.replaceAll(division -> {
            division = RemoveSquareBrackets.go(division.toString());
            return division;
        });
        customer_division.setItems(divisions);
    }
}
