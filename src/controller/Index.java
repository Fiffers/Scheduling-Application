package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import main.Main;
import model.Customer;

import java.net.URL;
import java.sql.*;

import java.util.ArrayList;
import java.util.ResourceBundle;

public class Index implements Initializable {
    @FXML private Label signed_in_as, sign_out;
    @FXML private TableView appointments_table, customers_table, contacts_table;

    static String DB_URL = "jdbc:mysql://localhost:3306/client_schedule?connectionTimeZone=SERVER";
    static String DB_USERNAME = "sqlUser";
    static String DB_PASSWORD = "Passw0rd!";

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
//        comboBoxAdd(appointment_dropdown, "SELECT Appointment_ID FROM appointments");
//        comboBoxAdd(customer_dropdown, "SELECT Customer_Name FROM customers");
//        comboBoxAdd(contact_dropdown, "SELECT Contact_Name FROM contacts");

    }

    public void changeIdToName (String id, String table) {

    }

    /**
     * Add data to tables dynamically
     * @param tableView The table object to have data added to
     * @param query The SQL query to receive the data to insert into the tableView object
     * @throws SQLException
     */
    public void tableViewInsertData(TableView tableView, String query) throws SQLException {
        ObservableList<Object> rowResults = FXCollections.observableArrayList();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData metadata = rs.getMetaData();
            int columnCount = metadata.getColumnCount();

            for (int i = 0; i < columnCount; i++) {
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1).replace("_", " ").replace(" ID", ""));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                tableView.getColumns().addAll(col);
            }

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= columnCount; i++) {

                    row.add(rs.getString(i));
                    if (i == 7 || i == 8) {
                        System.out.println(tableView.getItems());
                    }

                }
                rowResults.add(row);
            }
            tableView.setItems(rowResults);

        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }

    }



//    public void comboBoxAdd (TableView tableColumn, String query, String columnName1, String columnName2, String columnName3) {
//        ObservableList<String> items = FXCollections.observableArrayList();
//        try {
//            Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
//            Statement stmt = conn.createStatement();
//            ResultSet rs = stmt.executeQuery(query);
//            ResultSetMetaData metadata = rs.getMetaData();
//            Integer columnCount = metadata.getColumnCount();
//            for (int i = 1; i < columnCount; i++) {
//                String columnName = metadata.getColumnName(i);
//
//                while (rs.next()) {
//                    items.add(rs.getString(columnName));
//                    System.out.println(columnName);
//                }
//            }
//
//            rs.close();
//            stmt.close();
//            conn.close();
//        } catch (SQLException e) {
//            throw new IllegalStateException("Cannot connect the database!", e);
//        }
//        tableColumn.setItems(items);
//    }

//    public void appointmentSelect(ActionEvent actionEvent) throws Exception {
//        if (appointment_dropdown.getValue() != addString) {
//            appointment_edit_button.setText("View");
//        }
//        else {
//            appointment_edit_button.setText("Add");
//        }
//    }
//
//    public void customerSelect(ActionEvent actionEvent) throws Exception {
//        if (customer_dropdown.getValue() != addString) {
//            customer_edit_button.setText("View");
//        }
//        else {
//            customer_edit_button.setText("Add");
//        }
//    }
//
//    public void contactSelect(ActionEvent actionEvent) throws Exception {
//        if (contact_dropdown.getValue() != addString) {
//            contact_edit_button.setText("View");
//        }
//        else {
//            contact_edit_button.setText("Add");
//        }
//    }



    public void addAppointment(ActionEvent actionEvent) throws Exception {

    }

    public void addContact(ActionEvent actionEvent) throws Exception {

    }
    public void addCustomer(ActionEvent actionEvent) throws Exception {

    }

    public void editAppointment(ActionEvent actionEvent) throws Exception {

    }

    public void editContact(ActionEvent actionEvent) throws Exception {

    }
    public void editCustomer(ActionEvent actionEvent) throws Exception {

    }


    public void addUnderline(MouseEvent mouseEvent) {
        sign_out.setUnderline(true);
    }

    public void removeUnderline(MouseEvent mouseEvent) {
        sign_out.setUnderline(false);
    }
}
