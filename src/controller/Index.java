package controller;

import database.DBConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import javafx.util.Callback;
import main.Main;
import database.DBInteraction;

import java.io.IOException;
import java.net.URL;
import java.sql.*;

import java.util.Objects;
import java.util.ResourceBundle;

import static utilities.Print.print;

public class Index implements Initializable {
    @FXML private Label signed_in_as, sign_out;
    @FXML private TableView appointments_table, customers_table, contacts_table;
    @FXML private Button appointment_edit_button, customer_edit_button, contact_edit_button;


    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        signed_in_as.setText("Signed in as: " + Main.username);

        try {
            tableViewInsertData(appointments_table, "SELECT Appointment_ID, Title, Description, Location, Type, Start, End, customers.Customer_Name, contacts.Contact_Name FROM appointments INNER JOIN customers on appointments.Customer_ID=customers.Customer_ID JOIN contacts on appointments.Contact_ID=contacts.Contact_ID");
            tableViewInsertData(customers_table, "SELECT Customer_Name, Address, Postal_Code, Phone, Division_ID FROM customers");
            tableViewInsertData(contacts_table, "SELECT Contact_Name, Email FROM contacts");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Add data to tables dynamically
     * @param tableView The table object to have data added to
     * @param string The SQL query to receive the data to insert into the tableView object
     * @throws SQLException
     */
    public void tableViewInsertData(TableView tableView, String string) throws SQLException {
        ObservableList<Object> rowResults = DBInteraction.query(string);

        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(string);
            ResultSet result = ps.executeQuery();

            for (int i = 0; i < result.getMetaData().getColumnCount(); i++) {
                final int j = i;
                TableColumn column = new TableColumn(result.getMetaData().getColumnName(i + 1).replace("_", " ").replace(" ID", ""));
                column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        String cellText = param.getValue().get(j).toString();
                        return new SimpleStringProperty(cellText);
                    }
                });
                /** Make sure the primary key is hidden in the tableview, but is still accessible */
                if (Objects.equals(column.textProperty().getValue(), "Appointment")) {
                    tableView.getColumns().addAll(column);
                    column.setVisible(false);
                }
                else {
                    tableView.getColumns().addAll(column);
                }
            }
            tableView.setItems(rowResults);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addAppointment(ActionEvent event) throws Exception {
        SceneController.changeScene("/view/AddEditAppointment.fxml", "Add Appointment", event);
    }
    public void addContact(ActionEvent event) throws Exception {    }
    public void addCustomer(ActionEvent event) throws Exception {    }
    public void editAppointment(ActionEvent event) throws Exception {
        ObservableList selectedItems = appointments_table.getSelectionModel().getSelectedItems();
        SceneController.changeScene("/view/AddEditAppointment.fxml", "Add Appointment", event);
    }
    public void editContact(ActionEvent event) throws Exception {    }
    public void editCustomer(ActionEvent event) throws Exception {    }
    public void addUnderline(MouseEvent event) {
        sign_out.setUnderline(true);
    }
    public void removeUnderline(MouseEvent event) {
        sign_out.setUnderline(false);
    }
    public void selectAppointment(MouseEvent event) throws IOException {
        Main.selectedAppointment = appointments_table.getSelectionModel().getSelectedItems();
        appointment_edit_button.setDisable(false);
    }

}
