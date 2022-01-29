package controller;

import database.DBConnection;

import database.DBInteraction;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import main.Main;
import utilities.InputValidator;
import utilities.Popup;
import utilities.RemoveSquareBrackets;

public class AddEditAppointment {
    @FXML private TextField appointment_id, appointment_title, appointment_location;
    @FXML private TextField appointment_start_hours, appointment_start_minutes;
    @FXML private TextField appointment_end_hours, appointment_end_minutes;
    @FXML private TextArea appointment_description;
    @FXML private DatePicker appointment_date;
    @FXML private ComboBox appointment_customer, appointment_contact;
    @FXML private Label appointment_label;
    @FXML private ChoiceBox appointment_start_ampm, appointment_end_ampm;
    @FXML private StackPane appointment_pane;
    @FXML private VBox vbox_2;

    public void initialize() throws SQLException {
        final String[][] array = {null};
        if (Main.updateDatabase == true) {
            try {

                Main.selectedAppointment.forEach((appointment) -> {
                    String selectedRow = RemoveSquareBrackets.go(appointment.toString());
                    array[0] = selectedRow.split(",");
                });
                String string = "SELECT * FROM appointments WHERE appointment_id = '" + array[0][0] + "'";
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(string);
                ResultSet result = ps.executeQuery();
                while (result.next()) {
                    appointment_label.setText("Edit Appointment");

                    appointment_id.setText(result.getString("Appointment_ID"));
                    appointment_title.setText(result.getString("Title"));
                    appointment_location.setText(result.getString("Location"));
                    appointment_description.setText(result.getString("Description"));

                    String startDate[] = result.getString("Start").split(" ");
                    String endDate[] = result.getString("End").split(" ");
                    appointment_date.setValue(LocalDate.parse(startDate[0]));



                    String startTime[] = LocalTime.parse(startDate[1]).toString().split(":");
                    String endTime[] = LocalTime.parse(endDate[1]).toString().split(":");

                    appointment_start_hours.setText(startTime[0]);
                    appointment_start_minutes.setText(startTime[1]);

                    appointment_end_hours.setText(endTime[0]);
                    appointment_end_minutes.setText(endTime[1]);

                    for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
                        String columnName = result.getMetaData().getColumnName(i);

                        if (columnName.equals("Customer_ID")) {
                            String customerName = String.valueOf(DBInteraction.query("SELECT customer_name FROM customers WHERE customer_id = '" + result.getString(i) + "'"));
                            customerName = RemoveSquareBrackets.go(customerName);
                            appointment_customer.setValue(customerName);
                        }
                        if (columnName.equals("Contact_ID")) {
                            String contactName = String.valueOf(DBInteraction.query("SELECT contact_name FROM contacts WHERE contact_id = '" + result.getString(i) + "'"));
                            contactName = RemoveSquareBrackets.go(contactName);
                            appointment_contact.setValue(contactName);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Main.selectedAppointment = null;
            }
        }

        ObservableList customers = DBInteraction.query("SELECT customer_name FROM customers");
        ObservableList contacts = DBInteraction.query("SELECT contact_name FROM contacts");
        customers.replaceAll(customer -> {
            customer = RemoveSquareBrackets.go(customer.toString());
            return customer;
        });

        contacts.replaceAll(contact -> {
            contact = RemoveSquareBrackets.go(contact.toString());
            return contact;
        });

        appointment_customer.setItems(customers);
        appointment_contact.setItems(contacts);
    }

    public void cancelAppointment(ActionEvent event) throws Exception {
        SceneController.changeScene("/view/Index.fxml", "Scheduler", event, false);
    }

    public void saveAppointment(ActionEvent event) throws Exception {
        if (Main.updateDatabase) {
            int appointmentID = Integer.parseInt(appointment_id.getText());
            String appointmentLocation = appointment_location.getText();

            for (Node node: vbox_2.getChildren()) {
                if (node instanceof TextField) {
                    System.out.println(node);
                }
            }

            if (InputValidator.textFieldFilled(appointment_title, "a title") &&
                InputValidator.textFieldFilled(appointment_location, "a location") &&
                InputValidator.textFieldFilled(appointment_start_hours, "start hours") &&
                InputValidator.textFieldFilled(appointment_start_minutes, "start minutes") &&
                InputValidator.textFieldFilled(appointment_end_hours, "end hours") &&
                InputValidator.textFieldFilled(appointment_end_minutes, "end minutes")) {
                System.out.println("Valid");
            }
//            if (inputValidator()) {
//                DBInteraction.update("UPDATE appointments SET Location = '" + appointmentLocation + "' WHERE Appointment_ID = '" + appointmentID + "'");
//                SceneController.changeScene("/view/Index.fxml", "Scheduler", event, false);
//            }
        }
        else {

        }
    }

    public boolean inputValidator() {
        if (appointment_title.getText().trim().isEmpty()) {
            Popup.errorAlert("Error", "Make sure you input a title!");
            return false;
        }
        if (appointment_location.getText().trim().isEmpty()) {
            Popup.errorAlert("Error", "Make sure you input a location!");
            return false;
        }
        if (appointment_date.getEditor().getText().isEmpty()) {
            Popup.errorAlert("Error", "Make sure you input a date!");
            return false;
        }
        return true;
    }
}
