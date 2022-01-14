package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;

import static utilities.Print.print;

/**
 * This class is a wrapper for SQL queries
 */
public class Query {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/client_schedule?connectionTimeZone=SERVER";
    private static final String DB_USERNAME = "sqlUser";
    private static final String DB_PASSWORD = "Passw0rd!";

    /**
     * Resets the database to defaults defined in C195NewDB.sql
     * @throws Exception
     */
    public static void resetDatabase() throws Exception {
        try {
            String st;

            File file = new File("src/resources/C195NewDB.sql");
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((st = br.readLine()) != null) {
                if(!st.equals("")) {
                    String ch = Character.toString(st.charAt(0));
                    if (!ch.equals("-")) {
                        Query.updateDatabase(st);
                    }
                }
            }
            System.out.println("Database has been reset to defaults!");
        } catch (Exception e) {
            throw new Exception("Unable to reset database to defaults!", e);
        }
    }

    /**
     * Updates the database
     * @param string The SQL query to be executed
     * @throws SQLException
     */
    public static void updateDatabase(String string) throws SQLException {
        String splitString[] = string.split(" ", 2);
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD); Statement statement = connection.createStatement() ) {
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
    public static ObservableList<Object> queryDatabase(String string) throws SQLException {

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement statement = connection.createStatement();  ResultSet result = statement.executeQuery(string);) {

            ResultSetMetaData metadata    = result.getMetaData();
            ObservableList<Object> rowResults = FXCollections.observableArrayList();

            while (result.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= metadata.getColumnCount(); i++) {
                    row.add(result.getString(i));
                }
                rowResults.add(row);
            }
            print(rowResults);
            return rowResults;

        } catch (SQLException e) {
            throw new SQLException("Cannot connect the database!", e);
        }

    }

    /**
     * Authenticates the user
     * @param string The SQL query to be executed
     * @return whether user was successfully authenticated
     * @throws SQLException
     */
    public static boolean auth(String string) throws SQLException {
        boolean toggle = false;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(string)) {
            if (rs.next() == true) {
                Main.username = rs.getString("User_Name");
                Main.userID = Integer.valueOf(rs.getString("User_ID"));
                toggle = true;
            }
        } catch (SQLException e) {
            throw new SQLException("Unable to authorize this user!", e);
        }
        return toggle;
    }
}
