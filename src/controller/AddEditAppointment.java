package controller;

import database.DBConnection;

import database.DBInteraction;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.time.*;


import main.Main;
import utilities.*;

import javax.swing.*;

public class AddEditAppointment {
    @FXML private TextField appointment_id, appointment_title, appointment_location;
    @FXML private TextField appointment_start_hours, appointment_start_minutes;
    @FXML private TextField appointment_end_hours, appointment_end_minutes, appointment_type;
    @FXML private TextArea appointment_description;
    @FXML private DatePicker appointment_date;
    @FXML private ComboBox appointment_user, appointment_customer, appointment_contact;
    @FXML private Label appointment_label;

    public void initialize() throws SQLException {

        if (Main.updateDatabase) {
            appointment_label.setText("Edit Appointment");
            try {

                /** Set text for TextFields */
                appointment_id.setText(String.valueOf(Main.selectedAppointment.getAppointment_id()));
                appointment_title.setText(Main.selectedAppointment.getTitle());
                appointment_location.setText(Main.selectedAppointment.getLocation());
                appointment_description.setText(Main.selectedAppointment.getDescription());
                appointment_type.setText(Main.selectedAppointment.getType());

                /** Display local time */
                ZonedDateTime startDateTimeLocal = TimeZoneConverter.stringToZonedDateTime(Main.selectedAppointment.getStartUTC(), ZoneId.of("UTC"));
                ZonedDateTime endDateTimeLocal   = TimeZoneConverter.stringToZonedDateTime(Main.selectedAppointment.getEndUTC(), ZoneId.of("UTC"));

                startDateTimeLocal = TimeZoneConverter.toZone(startDateTimeLocal, ZoneId.systemDefault());
                endDateTimeLocal   = TimeZoneConverter.toZone(endDateTimeLocal, ZoneId.systemDefault());

                appointment_date.setValue(startDateTimeLocal.toLocalDate());

                appointment_start_hours.setText(PrependZero.twoDigits(startDateTimeLocal.getHour()));
                appointment_start_minutes.setText(PrependZero.twoDigits(startDateTimeLocal.getMinute()));

                appointment_end_hours.setText(PrependZero.twoDigits(endDateTimeLocal.getHour()));
                appointment_end_minutes.setText(PrependZero.twoDigits(endDateTimeLocal.getMinute()));

                /** Set value in ComboBoxes */
                appointment_user.setValue(Main.selectedAppointment.getUser_name());
                appointment_customer.setValue(Main.selectedAppointment.getCustomer_name());
                appointment_contact.setValue(Main.selectedAppointment.getContact_name());

                /** Get possible entries for each ComboBox, format and insert into respective ComboBox */
                DBInteraction.getComboBoxOptions("SELECT customer_name FROM customers", appointment_customer);
                DBInteraction.getComboBoxOptions("SELECT contact_name FROM contacts", appointment_contact);
                DBInteraction.getComboBoxOptions("SELECT user_name FROM users", appointment_user);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public void cancelAppointment(ActionEvent event) throws Exception {
        SceneController.changeScene("/view/Index.fxml", "Scheduler", event, false);
    }

    public void saveAppointment(ActionEvent event) throws Exception {
        LocalDate date       = appointment_date.getValue();
        String startHour     = appointment_start_hours.getText();
        String startMinute   = appointment_start_minutes.getText();
        String endHour       = appointment_end_hours.getText();
        String endMinute     = appointment_end_minutes.getText();

        boolean startTimeValid = InputValidator.isValidHour(startHour) && InputValidator.isValidMinute(startMinute);
        boolean endTimeValid   = InputValidator.isValidHour(endHour) && InputValidator.isValidMinute(endMinute);

        if (startTimeValid && endTimeValid) {
            String startDateTime = date + " " + startHour + ":" + startMinute + ":00";
            String endDateTime   = date + " " + endHour   + ":" + endMinute   + ":00";

            ZonedDateTime startZonedDateTime = TimeZoneConverter.stringToZonedDateTime(startDateTime, ZoneId.systemDefault());
            ZonedDateTime endZonedDateTime   = TimeZoneConverter.stringToZonedDateTime(endDateTime,   ZoneId.systemDefault());

            ZonedDateTime startTimeUTC = TimeZoneConverter.toZone(startZonedDateTime, ZoneId.of("UTC"));
            ZonedDateTime endTimeUTC   = TimeZoneConverter.toZone(endZonedDateTime,   ZoneId.of("UTC"));

            Timestamp startTime = TimeZoneConverter.toSQL(startTimeUTC);
            Timestamp endTime   = TimeZoneConverter.toSQL(endTimeUTC);
            Timestamp now       = Timestamp.from(Instant.now());

            boolean businessHours    =  InputValidator.isBusinessHours(startTimeUTC) &&
                                        InputValidator.isBusinessHours(endTimeUTC);

            boolean textFieldsFilled =  InputValidator.textFieldFilled(appointment_title, "a title") &&
                                        InputValidator.textFieldFilled(appointment_location, "a location") &&
                                        InputValidator.textFieldFilled(appointment_type, "an appointment type") &&
                                        InputValidator.textFieldFilled(appointment_start_hours, "start hours") &&
                                        InputValidator.textFieldFilled(appointment_start_minutes, "start minutes") &&
                                        InputValidator.textFieldFilled(appointment_end_hours, "end hours") &&
                                        InputValidator.textFieldFilled(appointment_end_minutes, "end minutes");

            boolean comboBoxesFilled =  InputValidator.comboBoxSelected(appointment_user, "a user") &&
                                        InputValidator.comboBoxSelected(appointment_customer, "a customer") &&
                                        InputValidator.comboBoxSelected(appointment_contact, "a contact");

            boolean datePickerFilled =  InputValidator.datePickerFilled(appointment_date, "a date");

            boolean textAreaFilled   =  InputValidator.textAreaFilled(appointment_description, "a description");

            boolean inputValid = textFieldsFilled && comboBoxesFilled && datePickerFilled && textAreaFilled && businessHours;

            String id          = appointment_id.getText();
            String title       = appointment_title.getText();
            String description = appointment_description.getText();
            String location    = appointment_location.getText();
            String type        = appointment_type.getText();
            String user        = appointment_user.getValue().toString();
            String customer    = appointment_customer.getValue().toString();
            String contact     = appointment_contact.getValue().toString();

            customer = String.valueOf(DBInteraction.simpleQuery("SELECT Customer_ID FROM customers WHERE Customer_Name = '" + customer + "'"));
            contact  = String.valueOf(DBInteraction.simpleQuery("SELECT Contact_ID FROM contacts WHERE Contact_Name = '" + contact + "'"));
            user     = String.valueOf(DBInteraction.simpleQuery("SELECT User_ID FROM users WHERE User_Name = '" + user + "'"));

            String query;
            if (id.equals("") && inputValid) {
                query = "INSERT INTO appointments (Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) " +
                        "VALUES ('" + title + "', '" + description + "', '" + location + "', '" + type + "', '" +
                        startTime + "', '" + endTime + "', '" + now + "', '" + Main.username + "', '" + now
                        + "', '" + Main.username + "', '" + customer + "', '" + user  +
                        "', '" + contact + "')";
                DBInteraction.update(query);
                SceneController.changeScene("/view/Index.fxml", "Scheduler", event, false);
            }
            else if (inputValid) {
                query = "UPDATE appointments SET Title = '" + title + "', Description = '" + description + "', " +
                        "Location = '" + location + "', Type = '" + type + "', Start = '" + startTime + "', End = '" + endTime + "', " +
                        "Last_Update = '" + now + "', Last_Updated_By = '" + Main.username + "', Customer_ID = '" + customer + "', " +
                        "User_ID = '" + user + "', Contact_ID = '" + contact + "'" +
                        "WHERE Appointment_ID = '" + id + "'";
                DBInteraction.update(query);
                SceneController.changeScene("/view/Index.fxml", "Scheduler", event, false);
            }
        }
    }
}
