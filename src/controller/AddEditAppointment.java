package controller;

import database.DBConnection;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import main.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import static utilities.Print.print;

public class AddEditAppointment {
    @FXML private TextField appointment_id, appointment_title, appointment_location;
    @FXML private TextArea appointment_description;
    @FXML private DatePicker appointment_start, appointment_end;
    @FXML private ComboBox appointment_customer, appointment_contact;

    public void initialize() {

        String selectedRow = Main.selectedAppointment.get(0).toString();

        selectedRow = selectedRow.replace("[","").replace("]","").replace(" ", "");
        String[] array = selectedRow.split(",");

        try {
            String string = "SELECT * FROM appointments WHERE appointment_id = '" + array[0] + "'";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(string);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
                    String columnName = result.getMetaData().getColumnName(i);
                    if (columnName.equals("Appointment_ID")) {
                        appointment_id.setText(result.getString(i));
                    }
                    if (columnName.equals("Title")) {
                        appointment_title.setText(result.getString(i));
                    }
                    if (columnName.equals("Description")) {
                        appointment_description.setText(result.getString(i));
                    }
                    if (columnName.equals("Location")) {
                        appointment_location.setText(result.getString(i));
                    }
//                    if (columnName.equals("Type")) {
//                        appointment_title.setText(result.getString(i));
//                    }
//                    if (columnName.equals("Start")) {
//                        appointment_start.setDayCellFactory(result.getString(i));
//                        appointment_start.setValue(LocalDate.parse(result.getString(i)));
//                    }
//                    if (columnName.equals("End")) {
//                        appointment_title.setText(result.getString(i));
//                    }

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
