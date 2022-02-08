package database;

import javafx.scene.control.ComboBox;
import main.Main;

import java.sql.*;
import java.time.*;
import java.time.temporal.ChronoUnit;

/**
 * This class is a wrapper for SQL queries
 */
public class DBInteraction {
    /**
     * Updates the database with DELETE, INSERT, or UPDATE statements
     * @param string The SQL query to be executed
     */
    public static void update(String string) throws SQLException {
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            statement.executeUpdate(string);
        } catch (SQLException e) {
            throw new SQLException("Cannot connect to the database!", e);
        }
    }

    /**
     * Gets possible rows for combo-box and inserts them into it
     * @param string The SQL Query
     * @param comboBox The comboBox to insert rows into
     */
    public static void getComboBoxOptions(String string, ComboBox comboBox) {
        comboBox.getItems().clear();
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(string);
            ResultSet result = ps.executeQuery();

            while (result.next()) comboBox.getItems().add(result.getString(1));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setSelectedComboBoxOption(String string, ComboBox comboBox) {
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(string);
            ResultSet result = ps.executeQuery();

            while (result.next()) comboBox.setValue(result.getString(1));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String simpleQuery(String string) {
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(string);
            ResultSet result = ps.executeQuery();

            if (result.next()) return result.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return string;
    }

    /**
     * Authenticates the user
     * @param string The SQL query to be executed
     * @return whether user was successfully authenticated
     */
    public static boolean auth(String string) {
        boolean toggle = false;
        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(string);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
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
