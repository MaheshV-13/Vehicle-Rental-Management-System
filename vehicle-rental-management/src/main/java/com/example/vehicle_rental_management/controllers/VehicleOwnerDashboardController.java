package com.example.vehicle_rental_management.controllers;
import com.example.vehicle_rental_management.models.Vehicle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;
import java.util.*;


// THIS IS THE CODE FOR THE VEHICLE OWNER DASHBOARD

public class VehicleOwnerDashboardController {

    // This is to get the current username of the customer that is logged in for efficient organization of data
    private String getCurrentUsername() {
        return LoginController.loggedInUsername;
    }

    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TableColumn<Vehicle, String> plateColumn;
    @FXML private TableColumn<Vehicle, String> modelColumn;
    @FXML private TableColumn<Vehicle, String> colorColumn;
    @FXML private TableColumn<Vehicle, String> priceColumn;
    @FXML private TableColumn<Vehicle, Boolean> statusColumn;
    @FXML private Button logoutButton;
    @FXML
    private TextField plateField, modelField, colorField, priceField;
    @FXML
    private CheckBox availabilityCheckBox;
    @FXML private Button addVehicleButton, editVehicleButton, removeVehicleButton, viewHistoryButton, feedbackButton, confirmEditButton;

    // This method is to fill and load the vehicles into the dashboard when the stage loads
    @FXML
    public void initialize() {
        plateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPlate()));
        modelColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getModel()));
        colorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getColor()));
        priceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrice()));
        statusColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isAvailability()).asObject());
        loadVehicleList();
    }

    // This handler is for vehicle owners to add vehicles
    // Saves the added vehicles to vehicles.txt, vehicle_owners.txt, added_cars.txt
    // Error Handling DONE
    @FXML
    public void handleAddVehicle() {
        String username = getCurrentUsername();
        String plate = plateField.getText().trim();
        String model = modelField.getText().trim();
        String color = colorField.getText().trim();
        String price = priceField.getText().trim();
        boolean available = availabilityCheckBox.isSelected();

        if (plate.isEmpty() || model.isEmpty() || color.isEmpty() || price.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields must be filled.");
            return;
        }

        if (!isValidPrice(price)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid price format.");
            return;
        }

        if (isDuplicatePlate(plate)) {
            showAlert(Alert.AlertType.ERROR, "Error", "A vehicle with this plate number already exists.");
            return;
        }

        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Data/vehicles.txt", true))) {
                writer.write(plate + "," + model + "," + color + "," + price + "," + available);
                writer.newLine();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Data/vehicle_owners.txt", true))) {
                writer.write(username + "," + plate + "," + model + "," + color + "," + price + "," + available);
                writer.newLine();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Data/added_cars.txt", true))) {
                writer.write(username + "," + plate + "," + model + "," + color + "," + price + "," + available);
                writer.newLine();
            }

            showAlert(Alert.AlertType.INFORMATION, "Success", "Vehicle added successfully!");

            plateField.clear();
            modelField.clear();
            colorField.clear();
            priceField.clear();
            availabilityCheckBox.setSelected(false);

            loadVehicleList();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add vehicle.");
        }
    }

    private boolean isDuplicatePlate(String plate) {
        File file = new File("Data/vehicles.txt");

        if (!file.exists()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length > 0 && data[0].trim().equalsIgnoreCase(plate)) {
                    return true; // Duplicate found
                }
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to check for duplicate plates.");
        }

        return false;
    }

    // This method is error handling for price
    private boolean isValidPrice(String price) {
        return price.matches("^\\d+(\\.\\d{1,2})?$"); // Only numbers with optional decimal places
    }

    // This handler is for editing the information of vehicle owners existing vehicles
    // This is just for setting the text and doesn't save it yet as the edit is not confirmed
    // Error Handling DONE
    @FXML
    public void handleEditVehicle() {
        Vehicle selectedVehicle = vehicleTable.getSelectionModel().getSelectedItem();

        if (selectedVehicle == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a vehicle to edit.");
            return;
        }

        plateField.setText(selectedVehicle.getPlate());
        modelField.setText(selectedVehicle.getModel());
        colorField.setText(selectedVehicle.getColor());
        priceField.setText(selectedVehicle.getPrice());
        availabilityCheckBox.setSelected(selectedVehicle.isAvailability());
    }

    // This is the handler for confirming the edit
    // Actually saves/writes data to vehicles.txt, vehicle_owners.txt, added_cars.txt by calling updateVehicleInFile()
    // Updates the table in the window with the newly added vehicles
    // Error Handling DONE
    @FXML
    public void handleConfirmEdit() {
        String username = getCurrentUsername();
        String model = modelField.getText().trim();
        String color = colorField.getText().trim();
        String price = priceField.getText().trim();
        String newPlate = plateField.getText().trim();
        boolean available = availabilityCheckBox.isSelected();

        if (model.isEmpty() || color.isEmpty() || price.isEmpty() || newPlate.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            return;
        }

        try {
            Double.parseDouble(price);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid price.");
            return;
        }

        Vehicle selectedVehicle = vehicleTable.getSelectionModel().getSelectedItem();
        if (selectedVehicle != null) {
            String oldPlate = selectedVehicle.getPlate();

            try {
                updateVehicleInFile("Data/vehicles.txt", "", oldPlate, newPlate, model, color, price, available);
                updateVehicleInFile("Data/vehicle_owners.txt", username, oldPlate, newPlate, model, color, price, available);
                updateVehicleInFile("Data/added_cars.txt", username, oldPlate, newPlate, model, color, price, available);

                selectedVehicle.setPlate(newPlate);
                selectedVehicle.setModel(model);
                selectedVehicle.setColor(color);
                selectedVehicle.setPrice(price);
                selectedVehicle.setAvailability(available);

                vehicleTable.refresh();

                showAlert(Alert.AlertType.INFORMATION, "Success", "Vehicle details updated successfully!");
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update vehicle details.");
                e.printStackTrace();
            }
        }
    }

    // This method updates vehicles.txt, vehicle_owners.txt, added_cars.txt with the added vehicles
    // Error handling DONE
    private void updateVehicleInFile(String filePath, String username, String oldPlate, String newPlate, String model, String color, String price, boolean available) throws IOException {
        File inputFile = new File(filePath);
        File tempFile = new File("Data/temp_" + new File(filePath).getName());

        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if (filePath.equals("Data/vehicles.txt") && data[0].equals(oldPlate)) {
                    writer.write(newPlate + "," + model + "," + color + "," + price + "," + available);
                    updated = true;
                    System.out.println("Updated plate in " + filePath + ": " + oldPlate + " -> " + newPlate);
                } else if ((filePath.equals("Data/vehicle_owners.txt") || filePath.equals("Data/added_cars.txt"))
                        && data.length >= 2 && data[1].equals(oldPlate) && data[0].equals(username)) {
                    writer.write(username + "," + newPlate + "," + model + "," + color + "," + price + "," + available);
                    updated = true;
                    System.out.println("Updated plate in " + filePath + ": " + oldPlate + " -> " + newPlate);
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }

            if (!updated) {
                writer.write(username + "," + newPlate + "," + model + "," + color + "," + price + "," + available);
                writer.newLine();
                System.out.println("Added new entry to " + filePath);
            }
        }

        if (!inputFile.delete()) {
            System.out.println("Error: Failed to delete old file " + filePath);
        } else if (!tempFile.renameTo(inputFile)) {
            System.out.println("Successfully updated " + filePath);
        }
    }

    // This is the handler for removing vehicles
    // Calls removeVehicleFromFile() to remove the vehicles from the text files
    // Also removes the vehicles from the table in the window
    // Error handling DONE
    public void handleRemoveVehicle() {
        Vehicle selectedVehicle = vehicleTable.getSelectionModel().getSelectedItem();
        if (selectedVehicle == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a vehicle to remove.");
            return;
        }

        String plate = selectedVehicle.getPlate();
        System.out.println("Attempting to remove vehicle with plate: " + plate);

        try {
            removeVehicleFromFile("Data/vehicles.txt", plate);
            removeVehicleFromFile("Data/vehicle_owners.txt", plate);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Vehicle removed successfully!");
            vehicleTable.getItems().remove(selectedVehicle);
            vehicleTable.refresh();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to remove vehicle.");
            e.printStackTrace();
        }
    }

    // This method is for removing vehicles from vehicles.txt and vehicle_owners.txt
    // Error Handling DONE
    private void removeVehicleFromFile(String filePath, String plate) throws IOException {
        File inputFile = new File(filePath);
        File tempFile = new File("Data/temp_" + new File(filePath).getName());

        boolean removed = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));

             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if ((filePath.equals("Data/vehicles.txt") && data[0].equals(plate)) ||
                        (filePath.equals("Data/vehicle_owners.txt") && data.length > 1 && data[1].equals(plate))) {
                    removed = true;
                    System.out.println("Removed vehicle: " + line);
                    continue;
                }
                writer.write(line);
                writer.newLine();
            }
        }

        if (!removed) {
            System.out.println("Vehicle with plate " + plate + " not found in " + filePath);
            throw new IOException("Vehicle not found in " + filePath);
        }

        if (!inputFile.delete()) {
            throw new IOException("Failed to delete old file: " + filePath);
        }
        if (!tempFile.renameTo(inputFile)) {
            throw new IOException("Failed to rename temp file: " + tempFile.getName());
        }
    }

    // This handler is for vehicle owners to view their added vehicles history
    // Opens a pop-up window and displays the history
    // History is read from added_cars.txt
    // Error Handling DONE
    @FXML
    public void handleViewHistory() {
        String username = getCurrentUsername();
        Stage historyStage = new Stage();
        VBox historyLayout = new VBox();
        historyLayout.setSpacing(10);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPrefHeight(300);

        VBox historyContent = new VBox();
        historyContent.setSpacing(5);

        try (BufferedReader reader = new BufferedReader(new FileReader("Data/added_cars.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals(username)) {
                    Label historyLabel = new Label("Plate: " + data[1] + ", Model: " + data[2] + ", Color: " + data[3] + ", Price: " + data[4] + ", Available: " + data[5]);
                    historyContent.getChildren().add(historyLabel);
                }
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load history.");
        }

        scrollPane.setContent(historyContent);
        historyLayout.getChildren().add(scrollPane);

        Scene historyScene = new Scene(historyLayout, 400, 400);
        historyStage.setScene(historyScene);
        historyStage.setTitle("Vehicle Adding History");
        historyStage.show();
    }

    // This handler creates a pop-up windows for vehicle owners to give feedbacks
    // submitFeedback() method is then called to actually save the feedback
    // Error Handling DONE
    @FXML
    public void handleFeedback() {
        Stage feedbackStage = new Stage();
        VBox feedbackLayout = new VBox();
        feedbackLayout.setSpacing(10);

        TextArea feedbackTextArea = new TextArea();
        feedbackTextArea.setPromptText("Enter your feedback here...");
        feedbackLayout.getChildren().add(feedbackTextArea);

        Button submitButton = new Button("Submit Feedback");
        submitButton.setOnAction(e -> {
            String feedback = feedbackTextArea.getText().trim();
            if (feedback.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter a feedback message.");
            } else {
                submitFeedback(feedback);
                feedbackStage.close();
            }
        });

        feedbackLayout.getChildren().add(submitButton);

        Scene feedbackScene = new Scene(feedbackLayout, 400, 300);
        feedbackStage.setScene(feedbackScene);
        feedbackStage.setTitle("Submit Feedback");
        feedbackStage.show();
    }

    // This method saves/writes the feedback into reports.txt
    // Error handling DONE
    private void submitFeedback(String feedback) {
        String username = getCurrentUsername();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Data/reports.txt", true))) {
            writer.write(username + "," + feedback);
            writer.newLine();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Feedback submitted successfully!");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to submit feedback.");
        }
    }

    // This method reads vehicle_owners.txt and stores the data in an ArrayList
    // Error Handling DONE
    private void loadVehicleList() {
        vehicleTable.getItems().clear();
        String username = getCurrentUsername();
        List<Vehicle> vehicles = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("Data/vehicle_owners.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length == 6 && data[0].equals(username)) { // Ensure the data format is correct
                    String plate = data[1];
                    String model = data[2];
                    String color = data[3];
                    String price = data[4];
                    boolean available = Boolean.parseBoolean(data[5]);

                    vehicles.add(new Vehicle(plate, model, color, price, available));
                }
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load vehicle list.");
        }

        vehicleTable.getItems().addAll(vehicles);
    }

    // This is the handler for the logout button
    // Redirects back to login screen
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
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
