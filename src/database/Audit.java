package database;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;

public class Audit {
    /**
     * Keeps an audit log of all login attempts and stores the data into a local text file
     * @param time The time of the login attempt in UTC
     * @param date The date of the login attempt in UTC
     * @param success Whether or not the login attempt was a success
     * @throws IOException
     */
    public static void loginAudit( LocalTime time, LocalDate date, boolean success) throws IOException {
        Path path = Paths.get("login_activity.txt");

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
            String result = "Date: " + date + " | Time: " + time + "Z | Success: " + success;
            writer.write(result);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
