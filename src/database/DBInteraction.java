package database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.scene.control.ComboBox;
import main.Main;
import utilities.TimeZoneConverter;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;

/**
 * This class is a wrapper for SQL queries
 */
public class DBInteraction {
    /**
     * Updates the database with DELETE, INSERT, or UPDATE statements
     * @param string The SQL query to be executed
     * @throws SQLException
     */
    public static void update(String string) throws SQLException {
        String splitString[] = string.split(" ", 2);
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            if (splitString[0].equals("DELETE") || splitString[0].equals("INSERT") || splitString[0].equals("UPDATE")) {
                statement.executeUpdate(string);
            }
        } catch (SQLException e) {
            throw new SQLException("Cannot connect to the database!", e);
        }
    }

    /**
     * Gets possible rows for combobox and inserts them into it
     * @param string The SQL Query
     * @param comboBox The comboBox to insert rows into
     */
    public static void getComboBoxOptions(String string, ComboBox comboBox) {
        comboBox.getItems().clear();
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(string);
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                comboBox.getItems().add(result.getString(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setSelectedComboBoxOption(String string, ComboBox comboBox) {
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(string);
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                comboBox.setValue(result.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String simpleQuery(String string) {
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(string);
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                return result.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Performs a SELECT query on the database
     * @param string The SQL query to be executed
     * @return The result of the SQL query
     */
    public static ObservableList qury(String string) {
        ObservableList rowResults = FXCollections.observableArrayList();
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(string);
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                ObservableList row = FXCollections.observableArrayList();
                for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
                    if (isTimestamp(result.getString(i))) {
                        ZonedDateTime zdt = TimeZoneConverter.stringToZonedDateTime(result.getString(i), ZoneId.of("UTC"));
                        zdt = TimeZoneConverter.toZone(zdt, ZoneId.systemDefault());
                        String zdtString = TimeZoneConverter.makeReadable(zdt);

                        row.add(zdtString);
                    }
                    else {
                        row.add(result.getString(i));
                    }
                }
                rowResults.add(row);

            }
            return rowResults;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowResults;
    }

    public static boolean isTimestamp(String string) {
        SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        boolean isTimestamp = false;
        try {
            format.parse(string);
            isTimestamp = true;
        } catch (Exception ignore) { }

        return isTimestamp;
    }

    /**
     * Authenticates the user
     * @param string The SQL query to be executed
     * @return whether user was successfully authenticated
     * @throws SQLException
     */
    public static boolean auth(String string) throws IOException {
        boolean toggle = false;
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(string);
            ResultSet result = ps.executeQuery();
            if (result.next() == true) {
                Main.username = result.getString("User_Name");
                Main.userID = Integer.valueOf(result.getString("User_ID"));
                System.out.println("User \"" + Main.username + "\" has been successfully authenticated!");
                toggle = true;

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        LocalTime timeTruncated = LocalTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS);
        LocalDate localDate = LocalDate.now(ZoneOffset.UTC);

        Audit.loginAudit(timeTruncated, localDate, toggle);
        return toggle;
    }
}
