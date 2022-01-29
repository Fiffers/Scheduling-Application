package utilities;

import javafx.scene.control.TextField;

public class InputValidator {

    public static boolean textFieldFilled(TextField textField, String string) {
        if (textField.getText().isEmpty()) {
            Popup.errorAlert("Error", "Make sure you input " + string + "!");
            return false;
        }
        return true;
    }
}
