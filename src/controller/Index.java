package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;


import main.Main;
import database.DBConnection;
import database.DBInteraction;
import model.Appointment;
import model.Contact;
import model.Customer;
import utilities.Popup;

import utilities.TimeZoneConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class Index implements Initializable {
    @FXML private Label signed_in_as, sign_out;
    @FXML private TableView<Appointment> appointments_table;
    @FXML private TableView<Customer> customers_table;
    @FXML private TableView<Contact> contacts_table;
    @FXML private Button appointment_edit, customer_edit, contact_edit, appointment_delete, customer_delete, contact_delete;

    ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    ObservableList<Customer>    customerList    = FXCollections.observableArrayList();
    ObservableList<Contact>     contactList     = FXCollections.observableArrayList();

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

        appointmentsTableInsertData(appointments_table_data);
        customersTableInsertData(customers_table_data);
        contactsTableInsertData(contacts_table_data);

    }

    public void buildColumns(ResultSet result, TableView tableView) throws SQLException {
        for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
            String columnName = result.getMetaData().getColumnName(i);
            TableColumn column = new TableColumn(columnName);
            column.setCellValueFactory(new PropertyValueFactory<Customer, String>(columnName.toLowerCase(Locale.ROOT)));
            if (Objects.equals(column.textProperty().getValue(), "Customer_ID") ||
                Objects.equals(column.textProperty().getValue(), "Contact_ID") ||
                Objects.equals(column.textProperty().getValue(), "Appointment_ID")) {
                column.setVisible(false);
            }
            tableView.getColumns().addAll(column);
        }
    }

    public void appointmentsTableInsertData(String string) {

        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(string);
            ResultSet result = ps.executeQuery();
            boolean toggle = true;

            while (result.next()) {

                Appointment appointment = new Appointment();
                appointment.setAppointment_id(result.getInt("Appointment_ID"));
                appointment.setTitle(result.getString("Title"));
                appointment.setDescription(result.getString("Description"));
                appointment.setLocation(result.getString("Location"));
                appointment.setType(result.getString("Type"));
                appointment.setCustomer_name(result.getString("Customer_Name"));
                appointment.setContact_name(result.getString("Contact_Name"));
                appointment.setUser_name(result.getString("User_Name"));
                appointment.setStartUTC(result.getString("Start"));
                appointment.setEndUTC(result.getString("End"));

                /* Convert string to ZonedDateTime in UTC */
                ZonedDateTime startZDT = TimeZoneConverter.stringToZonedDateTime(result.getString("Start"), ZoneId.of("UTC"));
                ZonedDateTime endZDT   = TimeZoneConverter.stringToZonedDateTime(result.getString("End"), ZoneId.of("UTC"));

                /* Convert ZDT to local time zone */
                startZDT = TimeZoneConverter.toZone(startZDT, ZoneId.systemDefault());
                endZDT   = TimeZoneConverter.toZone(endZDT, ZoneId.systemDefault());

                /* Format it into a user-friendly string for easier readability */
                String start = TimeZoneConverter.makeReadable(startZDT);
                String end   = TimeZoneConverter.makeReadable(endZDT);

                appointment.setStart(start);
                appointment.setEnd(end);
                appointmentList.setAll(appointment);

                while (toggle) {
                    buildColumns(result, appointments_table);
                    toggle = false;
                }
                appointments_table.getItems().add(appointment);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void customersTableInsertData(String string) {

        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(string);
            ResultSet result = ps.executeQuery();
            boolean toggle = true;

            while (result.next()) {

                Customer customer = new Customer();
                customer.setCustomer_id(result.getInt("Customer_ID"));
                customer.setCustomer_name(result.getString("Customer_Name"));
                customer.setAddress(result.getString("Address"));
                customer.setPostal_code(result.getString("Postal_Code"));
                customer.setPhone(result.getString("Phone"));
                customer.setDivision(result.getString("Division"));

                customerList.setAll(customer);

                while (toggle) {
                    buildColumns(result, customers_table);
                    toggle = false;
                }
                customers_table.getItems().add(customer);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void contactsTableInsertData(String string) {
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(string);
            ResultSet result = ps.executeQuery();
            boolean toggle = true;

            while (result.next()) {

                Contact contact = new Contact();
                contact.setContact_id(result.getInt("Contact_ID"));
                contact.setContact_name(result.getString("Contact_Name"));
                contact.setEmail(result.getString("Email"));

                contactList.setAll(contact);

                while (toggle) {
                    buildColumns(result, contacts_table);
                    toggle = false;
                }
                contacts_table.getItems().add(contact);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle the simplest of the button presses
     * Note: If the third argument in the SceneController.changeScene method == true, the application
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

    /** Make sign out link look like a normal hyperlink */
    public void addUnderline() {
        sign_out.setUnderline(true);
    }
    public void removeUnderline() {
        sign_out.setUnderline(false);
    }

    /** Handle user selection of a row in each tableview */
    public void selectAppointment() {
        Main.selectedAppointment = appointments_table.getSelectionModel().getSelectedItem();
        if (Main.selectedAppointment != null) {
            appointment_edit.setDisable(false);
            appointment_delete.setDisable(false);
        }
    }

    public void selectCustomer(){
        Main.selectedCustomer = customers_table.getSelectionModel().getSelectedItem();
        if (Main.selectedCustomer != null) {
            customer_edit.setDisable(false);
            customer_delete.setDisable(false);
        }
    }

    public void selectContact(){
        Main.selectedContact = contacts_table.getSelectionModel().getSelectedItem();
        if (Main.selectedContact != null) {
            contact_edit.setDisable(false);
            contact_delete.setDisable(false);
        }
    }

    /** Handle deletion of selected row in each tableview */
    public void deleteAppointment(Event event) throws SQLException, IOException {
        boolean result = Popup.confirmationAlert("Are you sure?", "Are you sure you'd like to cancel this appointment?");


        String firstLine = "The following appointment has been cancelled:\n";
        String id        = "Appointment ID: " + Main.selectedAppointment.getAppointment_id() + "\n";
        String title     = "Appointment Title: " + Main.selectedAppointment.getTitle() + "\n";
        String type      = "Appointment Type: " + Main.selectedAppointment.getType();

        if (result) {
            DBInteraction.update("DELETE FROM appointments WHERE Appointment_ID = '" + Main.selectedAppointment.getAppointment_id() + "'");
            Popup.informationAlert("Information", firstLine + id + title + type);
            SceneController.changeScene("/view/Index.fxml", "Scheduler", event, false);
        }
    }

    public void deleteCustomer(Event event) throws SQLException, IOException {

        boolean result = Popup.confirmationAlert("Are you sure?", "Are you sure you'd like to delete this customer?\n" +
                "Their respective appointments will also be deleted!");

        if (result) {
            DBInteraction.update("DELETE FROM customers WHERE Customer_ID = '" + Main.selectedCustomer.getCustomer_id() + "'");
            Popup.informationAlert("Information", "Customer " + Main.selectedCustomer.getCustomer_name() + " has been deleted!");
            SceneController.changeScene("/view/Index.fxml", "Scheduler", event, false);
        }
    }

    public void deleteContact(Event event) throws SQLException, IOException {
        boolean result = Popup.confirmationAlert("Are you sure?", "Are you sure you'd like to delete this contact?");

        if (result) {
            DBInteraction.update("DELETE FROM contacts WHERE Contact_ID = '" + Main.selectedContact.getContact_id() + "'");
            Popup.informationAlert("Information", "Contact " + Main.selectedContact.getContact_name() + " has been deleted!");
            SceneController.changeScene("/view/Index.fxml", "Scheduler", event, false);
        }
    }

    public void signOut(Event event) throws IOException {
        SceneController.changeScene("/view/Login.fxml", "Login", event, false);

        /* Empty global variables */
        Main.selectedAppointment = null;
        Main.selectedCustomer    = null;
        Main.selectedContact     = null;
        Main.username            = null;
        Main.userID              = null;
        Main.updateDatabase      = false;
    }
}
