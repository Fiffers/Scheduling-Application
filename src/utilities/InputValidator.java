package utilities;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
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
     * @param string The time to compare to business hours in 24hr format
     * @return Whether or not the provided time is between normal business hours
     */
    public static boolean isBusinessHours(String string) {
        //* Business hours are between 8 AM and 10 PM EST, including weekends. */
        return false;
    }

    public static boolean isCustomerOverlap() {
        return false;
    }
}
