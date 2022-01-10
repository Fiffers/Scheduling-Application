package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import main.Main;
import model.Customer;

import java.net.URL;
import java.sql.*;

import java.util.ArrayList;
import java.util.ResourceBundle;

public class Index implements Initializable {
    @FXML private ComboBox appointment_dropdown, customer_dropdown, contact_dropdown;
    @FXML private Button appointment_edit_button, customer_edit_button, contact_edit_button;
    @FXML private Label signed_in_as;

    static String DB_URL = "jdbc:mysql://localhost:3306/client_schedule?connectionTimeZone=SERVER";
    static String DB_USERNAME = "sqlUser";
    static String DB_PASSWORD = "Passw0rd!";

    String addString = "-----Add New-----";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        signed_in_as.setText("Signed in as: " + Main.username);

        comboBoxAdd(appointment_dropdown, "SELECT Appointment_ID FROM appointments");
        comboBoxAdd(customer_dropdown, "SELECT Customer_Name FROM customers");
        comboBoxAdd(contact_dropdown, "SELECT Contact_Name FROM contacts");
    }

    public void comboBoxAdd (ComboBox comboBox, String query) {
        ObservableList<String> items = FXCollections.observableArrayList();
        items.add(addString);
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData metadata = rs.getMetaData();
            String columnName = metadata.getColumnName(1);
            while (rs.next()) {
                items.add(rs.getString(columnName));
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
        comboBox.setItems(items);
    }

    public void appointmentSelect(ActionEvent actionEvent) throws Exception {
        if (appointment_dropdown.getValue() != addString) {
            appointment_edit_button.setText("View");
        }
        else {
            appointment_edit_button.setText("Add");
        }
    }

    public void customerSelect(ActionEvent actionEvent) throws Exception {
        if (customer_dropdown.getValue() != addString) {
            customer_edit_button.setText("View");
        }
        else {
            customer_edit_button.setText("Add");
        }
    }

    public void contactSelect(ActionEvent actionEvent) throws Exception {
        if (contact_dropdown.getValue() != addString) {
            contact_edit_button.setText("View");
        }
        else {
            contact_edit_button.setText("Add");
        }
    }

    public void addEditAppointment(ActionEvent actionEvent) throws Exception {

    }

    public void addEditContact(ActionEvent actionEvent) throws Exception {

    }
    public void addEditCustomer(ActionEvent actionEvent) throws Exception {

    }


}
