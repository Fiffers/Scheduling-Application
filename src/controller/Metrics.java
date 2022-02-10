package controller;

import database.DBConnection;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.Appointment;
import utilities.PrependZero;
import utilities.TimeZoneConverter;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Random;

public class Metrics {
    @FXML private Tab total_appointments;
    @FXML private ScrollPane contact_schedules, customer_schedules;
    VBox firstTab  = new VBox();
    VBox secondTab = new VBox();
    VBox thirdTab  = new VBox();

    /**
     * Calls all the methods we want and allows for the data to be dynamically inserted at runtime
     */
    public void initialize() {
        showAppointmentsByType();
        showAppointmentsByMonth();
        getScheduleByContact();
        getScheduleByCustomer();
//        firstTab.setMaxHeight(800);
        total_appointments.setContent(firstTab);
        contact_schedules.setContent(secondTab);
        customer_schedules.setContent(thirdTab);

    }

    /**
     * Returns the user to the index scene
     * @param event The button press
     */
    public void returnToIndex(Event event) throws IOException {
        SceneController.changeScene("/view/Index.fxml", "Index", event, false);
    }

    /**
     * Gets every appointment type in the database, and removes non-unique values
     * @return an array of strings, each value should be unique
     */
    public String[] getAppointmentTypes(){
        String string = "SELECT type FROM appointments";
        int appointmentsCount = getCount("SELECT COUNT(*) FROM appointments");
        try {
            int i = 0;
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(string);
            ResultSet result = ps.executeQuery();
            String[] typeArray = new String[appointmentsCount];
            while (result.next()) {
                typeArray[i] = result.getString("Type");
                i++;
            }

            return typeArray;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a bar chart of each month and the number of appointments in the database for that respective month
     * Lambda Expression - Allows iteration through each existing bar in the bar chart
     */
    public void showAppointmentsByMonth() {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> bc = new BarChart<>(xAxis, yAxis);
        bc.setPadding(new Insets(10));
        bc.setLegendVisible(false);

        bc.setTitle("Appointments by month");
        xAxis.setLabel("Month");
        yAxis.setLabel("Count");
        XYChart.Series series = new XYChart.Series();


        try {
            String query = "SELECT Start FROM appointments";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(query);
            ResultSet result = ps.executeQuery();
            int[] monthArray = new int[12];
            while (result.next()) {
                ZonedDateTime zdt = TimeZoneConverter.stringToZonedDateTime(result.getString("Start"), ZoneId.of("UTC"));
                zdt = TimeZoneConverter.toZone(zdt, ZoneId.systemDefault());

                int monthValue = zdt.getMonthValue() - 1;
                monthArray[monthValue] = monthArray[monthValue] + 1;
            }
            for (int i = 0; i < monthArray.length; i++) {
                String month = new DateFormatSymbols().getMonths()[i];
                series.getData().add(new XYChart.Data<>(month, monthArray[i]));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        bc.getData().add(series);
        firstTab.getChildren().add(bc);

        bc.lookupAll(".default-color0.chart-bar").forEach(bar -> {
            Random obj = new Random();
            int random = obj.nextInt(0xffffff + 1);
            String color = PrependZero.sixDigits(random);
            bar.setStyle("-fx-bar-fill: #" + color + ";");
        });
    }

    /**
     * Creates a bar chart of each type of appointment and the number of appointments in the database for that respective type
     * Lambda Expression - Allows iteration through each string in an array of strings
     */
    public void showAppointmentsByType() {

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> bc = new BarChart<>(xAxis, yAxis);
        bc.setPadding(new Insets(10));
        bc.setLegendVisible(false);
        bc.setTitle("Appointments by type");
        xAxis.setLabel("Type");
        yAxis.setLabel("Count");
        XYChart.Series series = new XYChart.Series();

        String[] appointmentTypes = getAppointmentTypes();

        Arrays.stream(appointmentTypes).distinct().forEach(type -> {
            try {
                String query = "SELECT COUNT(*) FROM appointments WHERE Type = '" + type + "'";
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(query);
                ResultSet result = ps.executeQuery();

                while (result.next()) {

                    series.getData().add(new XYChart.Data<>(type, result.getInt(1)));

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        bc.getData().add(series);
        firstTab.getChildren().add(bc);

        bc.lookupAll(".default-color0.chart-bar").forEach(bar -> {
            Random obj = new Random();
            int random = obj.nextInt(0xffffff + 1);
            String color = PrependZero.sixDigits(random);
            bar.setStyle("-fx-bar-fill: #" + color + ";");
        });
        Separator separator = new Separator();
        firstTab.getChildren().add(separator);
    }


    /**
     * Just gets the count result of an SQL query
     * @param query The SQL query i.e. SELECT COUNT(*) FROM table
     * @return The number of occurrences in the SQL table
     */
    public int getCount(String query) {
        int count = 0;
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(query);
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                count = result.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Gets an appointment schedule for each contact in the database and adds them in a tableview to a tab
     */
    public void getScheduleByContact() {

        int contactsCount = getCount("SELECT COUNT(*) FROM contacts");
        int[] contactIDs = new int[contactsCount];
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement("SELECT * FROM contacts");
            ResultSet result = ps.executeQuery();

            int i = 0;
            while (result.next()) {
                contactIDs[i] = Integer.parseInt(result.getString("Contact_ID"));
                i++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            boolean toggle = true;
            for (int id : contactIDs) {


                String query = "SELECT Appointment_ID, Title, Type, Description, " +
                        "Start, End, Customer_ID, contacts.Contact_Name FROM appointments " +
                        "JOIN contacts ON appointments.Contact_ID=contacts.Contact_ID " +
                        "WHERE appointments.Contact_ID ='" + id + "'";
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(query);

                ResultSet result = ps.executeQuery();

                while (result.next()) {
                    TableView contactTable = new TableView<Appointment>();
                    Appointment appointment = new Appointment();
                    appointment.setAppointment_id(result.getInt("Appointment_ID"));
                    appointment.setTitle(result.getString("Title"));
                    appointment.setType(result.getString("Type"));
                    appointment.setDescription(result.getString("Description"));
                    appointment.setStartUTC(result.getString("Start"));
                    appointment.setEndUTC(result.getString("End"));
                    appointment.setCustomer_id(result.getInt("Customer_ID"));
                    appointment.setContact_name(result.getString("Contact_Name"));

                    /* Convert string to ZonedDateTime in UTC */
                    ZonedDateTime startZDT = TimeZoneConverter.stringToZonedDateTime(result.getString("Start"), ZoneId.of("UTC"));
                    ZonedDateTime endZDT   = TimeZoneConverter.stringToZonedDateTime(result.getString("End"), ZoneId.of("UTC"));

                    /* Convert ZDT to local time zone */
                    startZDT = TimeZoneConverter.toZone(startZDT, ZoneId.systemDefault());
                    endZDT   = TimeZoneConverter.toZone(endZDT, ZoneId.systemDefault());

                    appointment.setStart(TimeZoneConverter.makeReadable(startZDT));
                    appointment.setEnd(TimeZoneConverter.makeReadable(endZDT));

                    Label contactName = new Label(appointment.getContact_name());
                    secondTab.getChildren().add(contactName);
                    while (toggle) {
                        Index.buildColumns(result, contactTable);
                        toggle = false;
                    }
                    contactTable.getItems().add(appointment);
                    contactTable.setMaxHeight(200);
                    contactTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
                    secondTab.getChildren().add(contactTable);
                    secondTab.setMinWidth(1190);
                    toggle = true;
                    Separator separator = new Separator();
                    secondTab.getChildren().add(separator);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Gets an appointment schedule for each customer in the database and adds them in a tableview to a tab
     */
    public void getScheduleByCustomer() {

        int customerCount = getCount("SELECT COUNT(*) FROM customers");
        int[] customerIDs = new int[customerCount];
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement("SELECT * FROM customers");
            ResultSet result = ps.executeQuery();

            int i = 0;
            while (result.next()) {
                customerIDs[i] = Integer.parseInt(result.getString("Customer_ID"));
                i++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            boolean toggle = true;
            for (int id : customerIDs) {


                String query = "SELECT Appointment_ID, Title, Type, Description, " +
                        "Start, End, User_ID, customers.Customer_Name FROM appointments " +
                        "JOIN customers ON appointments.Customer_ID=customers.Customer_ID " +
                        "WHERE appointments.Customer_ID ='" + id + "'";
                PreparedStatement ps = DBConnection.getConnection().prepareStatement(query);

                ResultSet result = ps.executeQuery();

                while (result.next()) {
                    TableView contactTable = new TableView<Appointment>();
                    Appointment appointment = new Appointment();
                    appointment.setAppointment_id(result.getInt("Appointment_ID"));
                    appointment.setTitle(result.getString("Title"));
                    appointment.setType(result.getString("Type"));
                    appointment.setDescription(result.getString("Description"));
                    appointment.setStartUTC(result.getString("Start"));
                    appointment.setEndUTC(result.getString("End"));
                    appointment.setCustomer_name(result.getString("Customer_Name"));

                    /* Convert string to ZonedDateTime in UTC */
                    ZonedDateTime startZDT = TimeZoneConverter.stringToZonedDateTime(result.getString("Start"), ZoneId.of("UTC"));
                    ZonedDateTime endZDT   = TimeZoneConverter.stringToZonedDateTime(result.getString("End"), ZoneId.of("UTC"));

                    /* Convert ZDT to local time zone */
                    startZDT = TimeZoneConverter.toZone(startZDT, ZoneId.systemDefault());
                    endZDT   = TimeZoneConverter.toZone(endZDT, ZoneId.systemDefault());

                    appointment.setStart(TimeZoneConverter.makeReadable(startZDT));
                    appointment.setEnd(TimeZoneConverter.makeReadable(endZDT));

                    Label customerName = new Label(appointment.getCustomer_name());
                    thirdTab.getChildren().add(customerName);
                    while (toggle) {
                        Index.buildColumns(result, contactTable);
                        toggle = false;
                    }
                    contactTable.getItems().add(appointment);
                    contactTable.setMaxHeight(200);
                    contactTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
                    thirdTab.getChildren().add(contactTable);
                    thirdTab.setMinWidth(1190);
                    toggle = true;
                    Separator separator = new Separator();
                    thirdTab.getChildren().add(separator);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
