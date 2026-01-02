package com.example.vehicle_rental_management.controllers;
import com.example.vehicle_rental_management.models.Vehicle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


// THIS IS THE CODE FOR THE ADMIN DASHBOARD

public class AdminDashboardController {

    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TableColumn<Vehicle, String> plateColumn;
    @FXML private TableColumn<Vehicle, String> modelColumn;
    @FXML private TableColumn<Vehicle, String> colorColumn;
    @FXML private TableColumn<Vehicle, String> priceColumn;
    @FXML private TableColumn<Vehicle, Boolean> statusColumn;
    @FXML private TextField searchField;
    @FXML private Button logoutButton;
    @FXML private Button manageUsersButton;
    @FXML private Button reportsAnalyticsButton;
    private final ObservableList<Vehicle> vehicleList = FXCollections.observableArrayList();

    // This will manually set the values for each column of the table
    // It also loads the vehicles by calling the loadVehicles() method
    @FXML
    public void initialize() {

        plateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPlate()));
        modelColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getModel()));
        colorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getColor()));
        priceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrice()));
        statusColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isAvailability()).asObject());

        loadVehicles();
    }

    // This will load the list of vehicles from the vehicles.txt and trim the information separated by ","
    // Error handling DONE
    private void loadVehicles() {
        vehicleList.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader("Data/vehicles.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 5) {
                    String plate = data[0].trim();
                    String model = data[1].trim();
                    String color = data[2].trim();
                    String price = data[3].trim();
                    boolean availability = Boolean.parseBoolean(data[4].trim());

                    Vehicle vehicle = new Vehicle(plate, model, color, price, availability);
                    vehicleList.add(vehicle);
                }
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load vehicles.");
        }
        vehicleTable.setItems(vehicleList);
    }

    // This is the handler for add button to add new vehicles
    // It saves the added vehicles into vehicles.txt by calling the saveVehicles() method
    @FXML
    private void handleAddVehicle() {
        VehicleFormController.showVehicleForm(null, newVehicle -> {
            vehicleList.add(newVehicle);
            vehicleTable.setItems(FXCollections.observableArrayList(vehicleList));
            saveVehicles();
        });
    }

    // This is the handler for edit button to edit vehicle information
    // You select a vehicles from available from the vehicles.txt file and edit the information
    // The saveVehicles() method will then be called to save the newly edited information into vehicles.txt
    // Error handling DONE
    @FXML
    private void handleEditVehicle() {
        Vehicle selectedVehicle = vehicleTable.getSelectionModel().getSelectedItem();
        if (selectedVehicle != null) {
            VehicleFormController.showVehicleForm(selectedVehicle, updatedVehicle -> {
                int index = vehicleList.indexOf(selectedVehicle);
                if (index != -1) {
                    vehicleList.set(index, updatedVehicle);
                    vehicleTable.setItems(FXCollections.observableArrayList(vehicleList));
                    saveVehicles();
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a vehicle to edit.");
        }
    }

    // This is the handler for the delete button to delete vehicles
    // This will delete only the selected vehicles from the vehicles.txt
    // saveVehicles() is called again to update the information into vehicles.txt
    // Error handling DONE
    @FXML
    private void handleDeleteVehicle() {
        Vehicle selectedVehicle = vehicleTable.getSelectionModel().getSelectedItem();
        if (selectedVehicle != null) {
            vehicleList.remove(selectedVehicle);
            vehicleTable.setItems(FXCollections.observableArrayList(vehicleList));
            saveVehicles();
        } else {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a vehicle to delete.");
        }
    }

    // This is the handler for the "Maintenance" button.
    // It basically allows the admin to decide whether the selected vehicle is available or not
    // saveVehicles() is called again to update the availability of the vehicle into vehicles.txt
    // Error handling DONE
    @FXML
    private void handleMarkMaintenance() {
        Vehicle selectedVehicle = vehicleTable.getSelectionModel().getSelectedItem();
        if (selectedVehicle != null) {
            selectedVehicle.setAvailability(false);
            vehicleTable.refresh();
            saveVehicles();
        } else {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a vehicle.");
        }
    }

    // This is the method that is used in the handlers to update the vehicles.txt
    // Error handling DONE
    private void saveVehicles() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Data/vehicles.txt"))) {
            for (Vehicle v : vehicleList) {
                writer.write(v.getPlate() + "," + v.getModel() + "," + v.getColor() + "," + v.getPrice() + "," + v.isAvailability());
                writer.newLine();
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save vehicles.");
        }
    }

    // This is the handler for the search & filter bar
    // Reads the ArrayList containing information from vehicles.txt
    // Determines whether the vehicle that is searched exists and available by comparing with the ArrayList
    // If yes, then it displays the vehicle
    // Error handling DONE
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();

        if (searchText.isEmpty()) {
            vehicleTable.setItems(vehicleList);
        } else {
            List<Vehicle> filteredVehicles = vehicleList.stream()
                    .filter(vehicle -> vehicle.getPlate().toLowerCase().contains(searchText) ||
                            vehicle.getModel().toLowerCase().contains(searchText) ||
                            vehicle.getColor().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());

            if (filteredVehicles.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No Results", "No vehicles match your search.");
            }
            vehicleTable.setItems(FXCollections.observableArrayList(filteredVehicles)); // Ensure this updates correctly
        }
    }

    // This is the handler for the manage users button
    // This opens a new window for the admin to manage the users
    // Error handling DONE.
    @FXML
    private void handleManageUsers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vehicle_rental_management/manage_users.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Manage Users");

            ManageUsersController controller = loader.getController();
            controller.loadUsers();

            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Manage Users window.");
        }
    }

    // This is the handler for the reports & analytics button
    // This opens a new window for admins to view customer feedbacks
    // Error handling DONE
    @FXML
    private void handleReportsAnalytics() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vehicle_rental_management/reports_and_analytics.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Reports & Analytics");
            stage.setScene(new Scene(root));
            stage.show();

            // Get the controller for Reports and Analytics and load the data
            ReportsAndAnalyticsController controller = loader.getController();
            controller.loadReportsAndAnalytics();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load Reports & Analytics window.");
        }
    }

    // This is the handler for the logout button
    // Redirects from admin dashboard back to login screen
    // Error handling DONE
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vehicle_rental_management/login.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            System.out.println("Error loading login page: " + e.getMessage());
        }
    }

    // This is a method used for showing pop-up alerts message
    // It is called whenever needed
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
