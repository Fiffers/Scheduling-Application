package controller;

import database.DBConnection;
import database.DBInteraction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.Main;
import model.Appointment;
import model.Contact;
import model.Customer;
import utilities.Popup;
import utilities.TimeZoneConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

public class Index implements Initializable {
    @FXML private Label signed_in_as, sign_out, upcoming_appointments;
    @FXML private TableView<Appointment> appointments_table;
    @FXML private TableView<Customer> customers_table;
    @FXML private TableView<Contact> contacts_table;
    @FXML private Button appointment_edit, customer_edit, contact_edit, appointment_delete, customer_delete, contact_delete;
    @FXML private ToggleButton filter_enable;
    @FXML private RadioButton filter_week, filter_month;

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

    /**
     * Builds the columns for a tableview based on a result from an SQL query, then inserts them into a tableview
     * @param result The resultset used to determine number of columns and their names
     * @param tableView The tableview to add the columns into
     */
    public static void buildColumns(ResultSet result, TableView tableView) throws SQLException {
        for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
            String columnName = result.getMetaData().getColumnName(i);
            TableColumn column = new TableColumn(columnName);
            column.setCellValueFactory(new PropertyValueFactory<Customer, String>(columnName.toLowerCase(Locale.ROOT)));
            tableView.getColumns().addAll(column);
        }
    }

    /**
     * Gets data for appointments and inserts the data into the appointments_table tableview
     * @param string The SQL query to perform
     */
    public void appointmentsTableInsertData(String string) {
        appointments_table.getItems().clear();
        appointments_table.getColumns().clear();
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

                appointment.setStart(TimeZoneConverter.makeReadable(startZDT));
                appointment.setEnd(TimeZoneConverter.makeReadable(endZDT));

                ZonedDateTime currentZDT = ZonedDateTime.now(ZoneId.systemDefault());
                ZonedDateTime soonWindow = ZonedDateTime.now(ZoneId.systemDefault()).plusMinutes(15);

                boolean isStartBetween = startZDT.isAfter(currentZDT) && startZDT.isBefore(soonWindow);
                boolean isEndBetween   = endZDT.isAfter(currentZDT) && endZDT.isBefore(soonWindow);
                boolean isSameUser     = Main.username.equals(appointment.getUser_name());

                if ((isStartBetween || isEndBetween) && isSameUser) {
                    int delta;
                    if (startZDT.getHour() == currentZDT.getHour()) {
                        delta = startZDT.getMinute() - currentZDT.getMinute();
                    }
                    else {
                         int hourDelta = startZDT.getHour() - currentZDT.getHour();
                         delta = (startZDT.getMinute() - currentZDT.getMinute()) * hourDelta;
                    }
                    String deltaMinutes;
                    if (delta == 1) {
                        deltaMinutes = "Starts in " + delta + " minute";
                    }
                    else if (delta <= 0) {
                        deltaMinutes = "This appointment has already started!";
                    }
                    else {
                        deltaMinutes = "Starts in" + delta + " minutes";
                    }

                    upcoming_appointments.setText("Next Appointment: " + appointment.getTitle() + "\n" + deltaMinutes);
                    String informationString = "You have an appointment soon!\n" +
                            "Appointment ID: " + appointment.getAppointment_id() + "\n" +
                            "Title: " + appointment.getTitle() + "\n" +
                            "Type: " + appointment.getType() + "\n" +
                            "Start: " + appointment.getStart() + "\n" +
                            "End: " + appointment.getEnd();
                    Popup.informationAlert("Upcoming appointment!", informationString);
                }

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

    /**
     * Gets data for customer and inserts the data into the customers_table tableview
     * @param string The SQL query to perform
     */
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

    /**
     * Gets data for contacts and inserts the data into the contacts_table tableview
     * @param string The SQL query to perform
     */
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

    /**
     * Signs out the user by changing to the login scene and emptying any global variables
     * @param event The button press
     */
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

    /**
     * Fired when the filter_enable toggle is selected.
     * Disables and Enables other elements appropriately.
     */
    public void enableFilter() {
        if (filter_enable.isSelected()) {
            filter_week.setDisable(false);
            filter_month.setDisable(false);
            filterWeek();
        }
        else {
            filter_week.setDisable(true);
            filter_month.setDisable(true);
            appointmentsTableInsertData(appointments_table_data);
        }
        filter_week.setSelected(true);
        filter_month.setSelected(false);
    }

    /**
     * Fired when the filter_week radio button is selected.
     * Updates the appointments table with all appointments within the next week
     */
    public void filterWeek() {
        if (filter_enable.isSelected()) {
            filter_week.setSelected(true);
            filter_month.setSelected(false);
            filterAppointments(7);
        }
    }

    /**
     * Fired when the filter_month radio button is selected.
     * Updates the appointments table with all appointments within the next month
     */
    public void filterMonth() {
        if (filter_enable.isSelected()) {
            filter_month.setSelected(true);
            filter_week.setSelected(false);
            filterAppointments(30);
        }
    }

    /**
     * Filters out the appointments that aren't between two days and keeps the ones that are
     * @param days The number of days in the future you want to see appointments for
     */
    public void filterAppointments(int days) {
        Timestamp startWindow = TimeZoneConverter.toSQL(ZonedDateTime.now(ZoneId.of("UTC")));
        Timestamp endWindow = TimeZoneConverter.toSQL(ZonedDateTime.now(ZoneId.of("UTC")).plusDays(days));

        String query = appointments_table_data + " WHERE Start BETWEEN '" + startWindow + "' AND '" + endWindow + "'";
        appointmentsTableInsertData(query);
    }

    /**
     * Changes to the metrics scene
     * @param event The button press
     */
    public void viewMetrics(Event event) throws IOException {
        SceneController.changeScene("/view/Metrics.fxml", "Metrics", event,false);
    }
}
