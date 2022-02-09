package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DBConnection {
    private static Connection conn = null;

    /**
     * Our connection to the SQL Database
     */
    public static void startConnection() {
        ResourceBundle env = ResourceBundle.getBundle("env");
        String DB_URL = env.getString("DB_URL");
        String DB_USERNAME = env.getString("DB_USERNAME");
        String DB_PASSWORD = env.getString("DB_PASSWORD");
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            System.out.println("Database connected");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the connection
     * @return the connection
     */
    public static Connection getConnection() {
        return conn;
    }

    /**
     * Closes the connection
     */
    public static void closeConnection() {
        try {
            conn.close();
        } catch (Exception e) {
            // Don't care
        }
    }
}
