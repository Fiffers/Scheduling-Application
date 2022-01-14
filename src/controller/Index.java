package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import javafx.util.Callback;
import main.Main;
import model.Query;

import java.net.URL;
import java.sql.*;

import java.util.ResourceBundle;

public class Index implements Initializable {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/client_schedule?connectionTimeZone=SERVER";
    private static final String DB_USERNAME = "sqlUser";
    private static final String DB_PASSWORD = "Passw0rd!";

    @FXML private Label signed_in_as, sign_out;
    @FXML private TableView appointments_table, customers_table, contacts_table;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        signed_in_as.setText("Signed in as: " + Main.username);

        try {
            tableViewInsertData(appointments_table, "SELECT Title, Description, Location, Type, Start, End, Customer_ID, Contact_ID FROM appointments");
            tableViewInsertData(customers_table, "SELECT Customer_Name, Address, Postal_Code, Phone, Division_ID FROM customers");
            tableViewInsertData(contacts_table, "SELECT Contact_Name, Email FROM contacts");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Add data to tables dynamically
     * @param tableView The table object to have data added to
     * @param string The SQL query to receive the data to insert into the tableView object
     * @throws SQLException
     */
    public void tableViewInsertData(TableView tableView, String string) throws SQLException {
        ObservableList<Object> rowResults = Query.queryDatabase(string);

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             Statement statement = connection.createStatement();  ResultSet result = statement.executeQuery(string);){
            ResultSetMetaData metadata = result.getMetaData();
            int columnCount = metadata.getColumnCount();

            for (int i = 0; i < columnCount; i++) {
                final int j = i;
                TableColumn column = new TableColumn(result.getMetaData().getColumnName(i + 1).replace("_", " ").replace(" ID", ""));
                column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                tableView.getColumns().addAll(column);

            }
            tableView.setItems(rowResults);

        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
    }

    public void addAppointment(ActionEvent actionEvent) throws Exception {    }
    public void addContact(ActionEvent actionEvent) throws Exception {    }
    public void addCustomer(ActionEvent actionEvent) throws Exception {    }
    public void editAppointment(ActionEvent actionEvent) throws Exception {    }
    public void editContact(ActionEvent actionEvent) throws Exception {    }
    public void editCustomer(ActionEvent actionEvent) throws Exception {    }

    public void addUnderline(MouseEvent mouseEvent) {
        sign_out.setUnderline(true);
    }

    public void removeUnderline(MouseEvent mouseEvent) {
        sign_out.setUnderline(false);
    }
}
