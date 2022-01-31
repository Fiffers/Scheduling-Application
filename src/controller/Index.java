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

import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import main.Main;
import database.DBInteraction;
import utilities.Popup;
import utilities.RemoveSquareBrackets;

import java.io.IOException;
import java.net.URL;
import java.sql.*;

import java.util.Objects;
import java.util.ResourceBundle;

public class Index implements Initializable {
    @FXML private Label signed_in_as, sign_out;
    @FXML private TableView appointments_table, customers_table, contacts_table;
    @FXML private Button appointment_edit, customer_edit, contact_edit, appointment_delete, customer_delete, contact_delete;
    @FXML private AnchorPane appointments_container, customers_container, contacts_container;

    String appointments_table_data = "SELECT Appointment_ID, Title, Description, Location, Type, Start, End, " +
            "customers.Customer_Name, users.User_Name, contacts.Contact_Name FROM appointments " +
            "INNER JOIN customers on appointments.Customer_ID=customers.Customer_ID " +
            "JOIN contacts on appointments.Contact_ID=contacts.Contact_ID " +
            "JOIN users on appointments.User_ID=users.User_ID";
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

    /**
     * Handle the simplest of the button presses
     * Note: If the third argument in the SceneController method == true, the application
     *  assumes that the user wants to update an already existing row in the SQL database.
     *  This cuts the number of scenes we need to build in half, since we won't need to
     *  make both an add and edit scene for each appointment, customer, and contact.
     */
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

    /** Make signout link look like a normal hyperlink */
    public void addUnderline() {
        sign_out.setUnderline(true);
    }
    public void removeUnderline() {
        sign_out.setUnderline(false);
    }

    /** Handle user selection of a row in each tableview */
    public void selectAppointment() {
        Main.selectedAppointment = appointments_table.getSelectionModel().getSelectedItems();
        if (!Main.selectedAppointment.isEmpty()) {
            appointment_edit.setDisable(false);
            appointment_delete.setDisable(false);
        }
    }

    public void selectCustomer(){
        Main.selectedCustomer = customers_table.getSelectionModel().getSelectedItems();
        if (!Main.selectedCustomer.isEmpty()) {
            customer_edit.setDisable(false);
            customer_delete.setDisable(false);
        }
    }

    public void selectContact(){
        Main.selectedContact = contacts_table.getSelectionModel().getSelectedItems();
        if (!Main.selectedContact.isEmpty()) {
            contact_edit.setDisable(false);
            contact_delete.setDisable(false);
        }
    }

    /** Handle deletion of selected row in each tableview */
    public void deleteAppointment(Event event) throws SQLException, IOException {
        String selectedRow = Main.selectedAppointment.get(0).toString();
        selectedRow = RemoveSquareBrackets.go(selectedRow);
        String[] selectedRowArray = selectedRow.split(", ");
        boolean result = Popup.confirmationAlert("Are you sure?", "Are you sure you'd like to cancel this appointment?");
        System.out.println(selectedRowArray[4]);

        String firstLine = "The following appointment has been cancelled:\n";
        String id        = "Appointment ID: " + selectedRowArray[0] + "\n";
        String title     = "Appointment Title: " + selectedRowArray[1] + "\n";
        String type      = "Appointment Type: " + selectedRowArray[4];

        if (result) {
            DBInteraction.update("DELETE FROM appointments WHERE Appointment_ID = '" + selectedRowArray[0] + "'");
            Popup.informationAlert("Information", firstLine + id + title + type);
            SceneController.changeScene("/view/Index.fxml", "Scheduler", event, false);
        }
    }

    public void deleteCustomer(Event event) throws SQLException, IOException {
        String selectedRow = Main.selectedCustomer.get(0).toString();
        selectedRow = RemoveSquareBrackets.go(selectedRow);
        String[] selectedRowArray = selectedRow.split(", ");
        System.out.println(selectedRowArray[0]);
        boolean result = Popup.confirmationAlert("Are you sure?", "Are you sure you'd like to delete this customer?\n" +
                "Their respective appointments will also be deleted!");

        if (result) {
            DBInteraction.update("DELETE FROM customers WHERE Customer_ID = '" + selectedRowArray[0] + "'");
            Popup.informationAlert("Information", "Customer " + selectedRowArray[1] + " has been deleted!");
            SceneController.changeScene("/view/Index.fxml", "Scheduler", event, false);
        }
    }

    public void deleteContact(Event event) throws SQLException, IOException {
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
        SceneController.changeScene("/view/Login.fxml", "Login", event, false);

        /** Empty global variables */
        Main.selectedAppointment = null;
        Main.selectedCustomer    = null;
        Main.selectedContact     = null;
        Main.username            = null;
        Main.userID              = null;
        Main.updateDatabase      = false;
    }

    public void expandAppointments() {
        customers_container.getChildren().clear();
        contacts_container.getChildren().clear();


        double tableHeight = appointments_table.getHeight();
        appointments_table.setMinHeight(tableHeight*3);
    }

}
