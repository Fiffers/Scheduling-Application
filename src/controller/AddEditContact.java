package controller;

import javafx.event.ActionEvent;

public class AddEditContact {
    public void initialize() {

    }
    public void cancelContact(ActionEvent event) throws Exception {
        SceneController.changeScene("/view/Index.fxml", "Scheduler", event, false);
    }
    public void saveContact(ActionEvent event) throws Exception {
        // Do stuff
    }
}
