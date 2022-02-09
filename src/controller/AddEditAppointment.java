package controller;

import database.DBInteraction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.time.*;


import main.Main;
import model.Appointment;
import utilities.*;

public class AddEditAppointment {
    @FXML private TextField appointment_id, appointment_title, appointment_location;
    @FXML private TextField appointment_start_hours, appointment_start_minutes;
    @FXML private TextField appointment_end_hours, appointment_end_minutes, appointment_type;
    @FXML private TextArea appointment_description;
    @FXML private DatePicker appointment_date;
    @FXML private ComboBox appointment_user, appointment_customer, appointment_contact;
    @FXML private Label appointment_label;

    /**
     * Grabs all the data for a selected appointment, and inserts that data into their respective elements
     * to allow the user to edit them easily.
     */
    public void initialize() {

        if (Main.updateDatabase) {
            appointment_label.setText("Edit Appointment");
            try {

                /* Set text for TextFields */
                appointment_id.setText(String.valueOf(Main.selectedAppointment.getAppointment_id()));
                appointment_title.setText(Main.selectedAppointment.getTitle());
                appointment_location.setText(Main.selectedAppointment.getLocation());
                appointment_description.setText(Main.selectedAppointment.getDescription());
                appointment_type.setText(Main.selectedAppointment.getType());

                /* Display local time */
                ZonedDateTime startDateTimeLocal = TimeZoneConverter.stringToZonedDateTime(Main.selectedAppointment.getStartUTC(), ZoneId.of("UTC"));
                ZonedDateTime endDateTimeLocal   = TimeZoneConverter.stringToZonedDateTime(Main.selectedAppointment.getEndUTC(), ZoneId.of("UTC"));

                startDateTimeLocal = TimeZoneConverter.toZone(startDateTimeLocal, ZoneId.systemDefault());
                endDateTimeLocal   = TimeZoneConverter.toZone(endDateTimeLocal, ZoneId.systemDefault());

                appointment_date.setValue(startDateTimeLocal.toLocalDate());

                appointment_start_hours.setText(PrependZero.twoDigits(startDateTimeLocal.getHour()));
                appointment_start_minutes.setText(PrependZero.twoDigits(startDateTimeLocal.getMinute()));

                appointment_end_hours.setText(PrependZero.twoDigits(endDateTimeLocal.getHour()));
                appointment_end_minutes.setText(PrependZero.twoDigits(endDateTimeLocal.getMinute()));

                /* Set value in ComboBoxes */
                appointment_user.setValue(Main.selectedAppointment.getUser_name());
                appointment_customer.setValue(Main.selectedAppointment.getCustomer_name());
                appointment_contact.setValue(Main.selectedAppointment.getContact_name());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /* Get possible entries for each ComboBox, format and insert into respective ComboBox */
        DBInteraction.getComboBoxOptions("SELECT customer_name FROM customers", appointment_customer);
        DBInteraction.getComboBoxOptions("SELECT contact_name FROM contacts", appointment_contact);
        DBInteraction.getComboBoxOptions("SELECT user_name FROM users", appointment_user);

    }

    /**
     * Cancel editing/adding an appointment and return to the index scene
     * @param event The button press.
     */
    public void cancelAppointment(ActionEvent event) throws Exception {
        SceneController.changeScene("/view/Index.fxml", "Scheduler", event, false);
    }

    /**
     * Validates the data in several ways. First, all fields are checked to see if data was actually input,
     * then several other checks are made to ensure the users input meets the requirements of the application
     * @param event The button press
     */
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

            /* Here's where we make a bunch of booleans. If any are false, the data is not saved and the
               user will get an error popup that will explain the extent of the problem. */
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

            String appointmentID = "";
            if (!appointment_id.getText().equals("")) {
                appointmentID = appointment_id.getText().toString();
            }
            String customerID           =  DBInteraction.simpleQuery("SELECT Customer_ID FROM customers WHERE Customer_Name = '" + appointment_customer.getValue().toString() + "'");
            boolean noStartOverlap = InputValidator.noAppointmentOverlap(startTimeUTC, customerID, appointmentID);
            boolean noEndOverlap   = InputValidator.noAppointmentOverlap(endTimeUTC, customerID, appointmentID);
            boolean noOverlap = noStartOverlap && noEndOverlap;

            boolean startBeforeEnd = InputValidator.isStartBeforeEnd(startTimeUTC, endTimeUTC);

            /* Finally, all of those booleans are combined together into a literal megazord of a boolean
            *  Again, if its true, everything checks out!*/
            boolean inputValid = textFieldsFilled && comboBoxesFilled && datePickerFilled && textAreaFilled && businessHours && noOverlap && startBeforeEnd;

            Appointment appointment = new Appointment();
            /* Build the appointment object for later use */
            if (inputValid) {
                if (!appointment_id.getText().equals("")) {
                    appointment.setAppointment_id(Integer.parseInt(appointment_id.getText()));
                }
                appointment.setTitle(appointment_title.getText());
                appointment.setDescription(appointment_description.getText());
                appointment.setLocation(appointment_location.getText());
                appointment.setType(appointment_type.getText());
                appointment.setUser_name(appointment_user.getValue().toString());
                appointment.setCustomer_name(appointment_customer.getValue().toString());
                appointment.setContact_name(appointment_contact.getValue().toString());
                appointment.setCustomer_id(Integer.parseInt(customerID));
                appointment.setContact_id(Integer.parseInt(DBInteraction.simpleQuery("SELECT Contact_ID FROM contacts WHERE Contact_Name = '" + appointment.getContact_name() + "'")));
                appointment.setUser_id(Integer.parseInt(DBInteraction.simpleQuery("SELECT User_ID FROM users WHERE User_Name = '" + appointment.getUser_name() + "'")));
            }

            /* Checks if we're submitting a change to an existing row in the table or if we're adding a new row, then
            performs the appropriate SQL query */
            if (inputValid){
                String query;
                if (Main.updateDatabase) {
                    query = "UPDATE appointments SET Title = '" + appointment.getTitle() + "', Description = '" + appointment.getDescription() + "', " +
                            "Location = '" + appointment.getLocation() + "', Type = '" + appointment.getType() + "', Start = '" + startTime + "', End = '" + endTime + "', " +
                            "Last_Update = '" + now + "', Last_Updated_By = '" + Main.username + "', Customer_ID = '" + appointment.getCustomer_id() + "', " +
                            "User_ID = '" + appointment.getUser_id() + "', Contact_ID = '" + appointment.getContact_id() + "'" +
                            "WHERE Appointment_ID = '" + appointment.getAppointment_id() + "'";
                }
                else {
                    query = "INSERT INTO appointments (Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) " +
                            "VALUES ('" + appointment.getTitle() + "', '" + appointment.getDescription() + "', '" + appointment.getLocation() + "', '" + appointment.getType() + "', '" +
                            startTime + "', '" + endTime + "', '" + now + "', '" + Main.username + "', '" + now
                            + "', '" + Main.username + "', '" + appointment.getCustomer_id() + "', '" + appointment.getUser_id()  +
                            "', '" + appointment.getContact_id() + "')";

                }
                DBInteraction.update(query);
                SceneController.changeScene("/view/Index.fxml", "Scheduler", event, false);
            }


        }
    }
}
