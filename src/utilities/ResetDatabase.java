package utilities;

import model.Query;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ResetDatabase {
    /**
     * Resets the database to defaults defined in C195NewDB.sql
     * @throws Exception
     */
    public static void toDefaults() throws Exception {
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
}
