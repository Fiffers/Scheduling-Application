package database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.Main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static java.lang.Integer.parseInt;


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
     * Performs a SELECT query on the database
     * @param string The SQL query to be executed
     * @return The result of the SQL query
     * @throws SQLException
     */
    public static ObservableList<Object> query(String string) throws SQLException {
        ObservableList<Object> rowResults = null;

        try {
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(string);
            ResultSet result = ps.executeQuery();
            rowResults = FXCollections.observableArrayList();
            while (result.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();

                for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
                    if (isTimestamp(result.getString(i))) {
                        row.add(result.getTimestamp(i).toLocalDateTime().toString());
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
        System.out.println(rowResults);
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

    public static boolean isInt(String string) {
        boolean isInt = false;
        try {
            parseInt(string);
            isInt = true;
        } catch (Exception ignore) { }

        return isInt;
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
