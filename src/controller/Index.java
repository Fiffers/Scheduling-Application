package controller;

import database.DBConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import javafx.util.Callback;
import main.Main;
import database.DBInteraction;
import utilities.Popup;
import utilities.RemoveSquareBrackets;
import utilities.ResetDatabase;

import java.io.IOException;
import java.net.URL;
import java.sql.*;

import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class Index implements Initializable {
    @FXML private Label signed_in_as, sign_out;
    @FXML private TableView appointments_table, customers_table, contacts_table;
    @FXML private Button appointment_edit, customer_edit, contact_edit, appointment_delete, customer_delete, contact_delete;

    String appointments_table_data = "SELECT Appointment_ID, Title, Description, Location, Type, Start, End, " +
            "customers.Customer_Name, contacts.Contact_Name FROM appointments " +
            "INNER JOIN customers on appointments.Customer_ID=customers.Customer_ID " +
            "JOIN contacts on appointments.Contact_ID=contacts.Contact_ID";
    String customers_table_data = "SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone, " +
            "first_level_divisions.Division FROM customers " +
            "INNER JOIN first_level_divisions on customers.Division_ID=first_level_divisions.Division_ID";
    String contacts_table_data = "SELECT Contact_ID, Contact_Name,  Email FROM contacts";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        signed_in_as.setText("Signed in as: " + Main.username);

        try {
            tableViewInsertData(appointments_table, appointments_table_data);
            tableViewInsertData(customers_table, customers_table_data);
            tableViewInsertData(contacts_table, contacts_table_data);
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
                if (Objects.equals(column.textProperty().getValue(), "Appointment") ||
                    Objects.equals(column.textProperty().getValue(), "Customer") ||
                    Objects.equals(column.textProperty().getValue(), "Contact")) {
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
        SceneController.changeScene("/view/AddEditAppointment.fxml", "Add Appointment", event, false);
    }
    public void editAppointment(ActionEvent event) throws Exception {
        SceneController.changeScene("/view/AddEditAppointment.fxml", "Edit Appointment", event, true);
    }
    public void addCustomer(ActionEvent event) throws Exception {
        SceneController.changeScene("/view/AddEditCustomer.fxml", "Add Customer", event, false);
    }
    public void editCustomer(ActionEvent event) throws Exception {
        SceneController.changeScene("/view/AddEditCustomer.fxml", "Edit Customer", event, true);
    }
    public void addContact(ActionEvent event) throws Exception {
        SceneController.changeScene("/view/AddEditContact.fxml", "Add Contact", event, false);
    }
    public void editContact(ActionEvent event) throws Exception {
        SceneController.changeScene("/view/AddEditContact.fxml", "Edit Contact", event, true);
    }

    public void addUnderline(MouseEvent event) {
        sign_out.setUnderline(true);
    }
    public void removeUnderline(MouseEvent event) {
        sign_out.setUnderline(false);
    }

    public void selectAppointment(MouseEvent event) throws IOException {
        Main.selectedAppointment = appointments_table.getSelectionModel().getSelectedItems();
        appointment_edit.setDisable(false);
        appointment_delete.setDisable(false);
    }

    public void selectCustomer(MouseEvent event) throws IOException {
        Main.selectedCustomer = customers_table.getSelectionModel().getSelectedItems();
        customer_edit.setDisable(false);
        customer_delete.setDisable(false);
    }

    public void selectContact(MouseEvent event) throws IOException {
        Main.selectedContact = contacts_table.getSelectionModel().getSelectedItems();
        contact_edit.setDisable(false);
        contact_delete.setDisable(false);
    }

    public void deleteAppointment(Event event) throws SQLException, IOException {
        String selectedRow = Main.selectedAppointment.get(0).toString();
        selectedRow = RemoveSquareBrackets.go(selectedRow);
        String[] selectedRowArray = selectedRow.split(", ");
        boolean result = Popup.confirmationAlert("Are you sure?", "Are you sure you'd like to delete this appointment?");

        if (result) {
            DBInteraction.update("DELETE FROM appointments WHERE Appointment_ID = '" + selectedRowArray[0] + "'");
            SceneController.changeScene("/view/Index.fxml", "Scheduler", event, false);
        }
    }

    public void deleteCustomer(Event event) throws SQLException, IOException {
        /** Need to check to see if customer still has any appointments. If so, the appointments must be deleted first */
        String selectedRow = Main.selectedCustomer.get(0).toString();
        selectedRow = RemoveSquareBrackets.go(selectedRow);
        String[] selectedRowArray = selectedRow.split(", ");
        System.out.println(selectedRowArray[0]);
        boolean result = Popup.confirmationAlert("Are you sure?", "Are you sure you'd like to delete this customer?");

        if (result) {
            DBInteraction.update("DELETE FROM customers WHERE Customer_ID = '" + selectedRowArray[0] + "'");
            Popup.informationAlert("Information", "Customer " + selectedRowArray[1] + " has been deleted!");
            SceneController.changeScene("/view/Index.fxml", "Scheduler", event, false);
        }
    }

    public void deleteContact(Event event) throws SQLException, IOException {
        /** Need to check to see if customer still has any appointments. If so, the appointments must be deleted first */
        String selectedRow = Main.selectedContact.get(0).toString();
        selectedRow = RemoveSquareBrackets.go(selectedRow);
        String[] selectedRowArray = selectedRow.split(", ");
        System.out.println(selectedRowArray[0]);
        boolean result = Popup.confirmationAlert("Are you sure?", "Are you sure you'd like to delete this contact?");

        if (result) {
            DBInteraction.update("DELETE FROM contacts WHERE Contact_ID = '" + selectedRowArray[0] + "'");
            Popup.informationAlert("Information", "Contact " + selectedRowArray[1] + " has been deleted!");
            SceneController.changeScene("/view/Index.fxml", "Scheduler", event, false);
        }
    }

    public void signOut(Event event) throws IOException {
//    Do nothing..?
    }

}
