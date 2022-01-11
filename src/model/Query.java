package model;

import main.Main;

import java.sql.*;

import static utilities.Print.print;

/**
 * This class is a wrapper for SQL queries
 */
public class Query {
    static String DB_URL = "jdbc:mysql://localhost:3306/client_schedule?connectionTimeZone=SERVER";
    static String DB_USERNAME = "sqlUser";
    static String DB_PASSWORD = "Passw0rd!";

    public static String DBQuery(String string) throws Exception {

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {

            /** Opens connection to the SQL DB, and perform a query */
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(string);
            ResultSetMetaData metadata = result.getMetaData();

            /** Do stuff with the query */
            int columnCount = metadata.getColumnCount();

            while (result.next()) {
                print(result.getString("Password"));
            }


            /** Close the connection */
            result.close();
            statement.close();
            connection.close();
            return DB_URL;

        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }

    }

    public static boolean auth(String string) {
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
            throw new IllegalStateException("Cannot connect the database!", e);
        }
        return toggle;
    }

    public static String select (String row, String table, String column, String matches) throws Exception {

        String query = "SELECT " + row + " FROM " + table;
        if (matches != null && column != null) {
            query = query + " WHERE " + column + " = '" + matches + "'";
        }
        String result = Query.DBQuery(query);

        return result;
    }
}
