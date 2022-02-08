package utilities;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.FileNotFoundException;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.Scanner;

public class InputValidator {

    /**
     * Checks if the user actually filled in a textfield with text
     * @param textField The textfield to check
     * @param string The string provided by the user
     * @return true if the textfield is not empty
     */
    public static boolean textFieldFilled(TextField textField, String string) {
        if (textField.getText().isEmpty()) {
            Popup.errorAlert("Error", "Make sure you input " + string + "!");
            return false;
        }
        return true;
    }

    public static boolean textAreaFilled(TextArea textArea, String string) {
        if (textArea.getText().isEmpty()) {
            Popup.errorAlert("Error", "Make sure you input " + string + "!");
            return false;
        }
        return true;
    }

    /**
     * Checks if the user actually selected an option in a combobox
     * @param comboBox The combobox to check
     * @param string The string provided by the user
     * @return true if an option is selected
     */
    public static boolean comboBoxSelected(ComboBox comboBox, String string) {
        if (comboBox.getValue() == null) {
            Popup.errorAlert("Error", "Make sure you select " + string + "!");
            return false;
        }
        return true;
    }

    public static boolean datePickerFilled(DatePicker datePicker, String string) {
        if (datePicker.getValue() == null) {
            Popup.errorAlert("Error", "Make sure you select " + string + "!");
            return false;
        }
        return true;
    }

    /**
     * Checks provided email string for correctness
     * @param string The string to be checked
     * @return true if string is valid
     * @throws FileNotFoundException
     */
    public static boolean isEmail(String string) throws FileNotFoundException {
        if (string.contains("@") && string.contains(".")) {

            /** Split the email address up into parts */
            String domain = string.substring(string.indexOf("@") + 1);
            domain = domain.substring(0, domain.indexOf("."));
            String topLevelDomain = string.substring(string.indexOf(".") + 1);
            String userName = string.substring(0, string.indexOf("@"));

            /**
             *  Check if any of the substrings are empty, and validate TLD
             *  Note: A TLD is the text at the end of a URL. (.com, .org, etc.)
             */
            if (domain.isEmpty() || topLevelDomain.isEmpty() || userName.isEmpty() || !isValidTLD(topLevelDomain)) {
                Popup.errorAlert("Error", "Make sure you input the email correctly!");
                return false;
            }
            return true;
        }
        Popup.errorAlert("Error", "Make sure you input the email correctly!");
        return false;
    }

    public static boolean isPhoneNumber(String string) throws FileNotFoundException {
        string = string.replaceAll("-", "");
        try {
            Long.parseLong(string);
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Popup.errorAlert("Error", "Make sure you input the phone number correctly!");
            return false;
        }
    }

    public static boolean isValidHour(String string) {
        try {
            Integer.parseInt(string);
            if (Integer.valueOf(string) < 1 || Integer.valueOf(string) > 23) {
                Popup.errorAlert("Error", "Double check the hours you entered!");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Popup.errorAlert("Error", "Double check the hours you entered!");
            return false;
        }
    }
    public static boolean isValidMinute(String string) {
        try {
            Integer.parseInt(string);
            if (Integer.valueOf(string) < 0 || Integer.valueOf(string) > 59) {
                Popup.errorAlert("Error", "Double check the minutes you entered!");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Popup.errorAlert("Error", "Double check the minutes you entered!");
            return false;
        }
    }

    /**
     * Checks email Top Level Domain (TLD) against text file with list of possible TLDs
     * @param string The string to be compared
     * @return true if match was found
     * @throws FileNotFoundException
     */
    public static boolean isValidTLD(String string) throws FileNotFoundException {
        File file = new File("src/resources/tlds-alpha-by-domain.txt");
        Scanner input = new Scanner(file);
        while (input.hasNextLine()) {
            if (input.nextLine().equalsIgnoreCase(string)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to make sure appointment start and end times are between normal business hours
     * @param zdt The time to compare to business hours in 24hr format
     * @return true if the provided time is between normal business hours
     */
    public static boolean isBusinessHours(ZonedDateTime zdt) {
        /** Business hours are between 8 AM and 10 PM EST, including weekends. */
        ZonedDateTime converted = TimeZoneConverter.toZone(zdt, ZoneId.of("America/New_York"));
        ZonedDateTime openTime = TimeZoneConverter.stringToZonedDateTime("2000-01-01 08:00:00", ZoneId.of("America/New_York"));
        ZonedDateTime closeTime = TimeZoneConverter.stringToZonedDateTime("2000-01-01 22:00:00", ZoneId.of("America/New_York"));
        ZonedDateTime openOfBusiness = TimeZoneConverter.toZone(openTime, ZoneId.systemDefault());
        ZonedDateTime closeOfBusiness = TimeZoneConverter.toZone(closeTime, ZoneId.systemDefault());

        int openHour  = openOfBusiness.getHour();
        int closeHour = closeOfBusiness.getHour();

        String open = PrependZero.twoDigits(openHour) + "00";
        String close = PrependZero.twoDigits(closeHour) + "00";
        String errorString = "Normal business hours are between " + open + " and " + close + " (0800 to 2200 EST).\n" +
                "Your appointment must be within these hours!";
        if (converted.getHour() < 8 || converted.getHour() > 22) {
            Popup.errorAlert("Error", errorString);
            return false;
        }
        if (converted.getHour() == 22 && converted.getMinute() != 0) {
            Popup.errorAlert("Error", errorString);
            return false;
        }
        return true;
    }

    public static boolean isCustomerOverlap() {
        return false;
    }
}
