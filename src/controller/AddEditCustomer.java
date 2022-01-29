package controller;

import javafx.event.ActionEvent;

public class AddEditCustomer {
    public void initialize() {

    }
    public void cancelCustomer(ActionEvent event) throws Exception {
        SceneController.changeScene("/view/Index.fxml", "Scheduler", event, false);
    }
    public void saveCustomer(ActionEvent event) throws Exception {
        // Do stuff
    }
}
