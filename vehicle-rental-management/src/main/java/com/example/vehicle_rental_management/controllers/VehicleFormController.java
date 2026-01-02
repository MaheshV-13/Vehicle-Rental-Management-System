package com.example.vehicle_rental_management.controllers;
import com.example.vehicle_rental_management.models.Vehicle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.function.Consumer;


// THIS IS THE CODE FOR ADMINS TO ADD OR MANAGE/EDIT THEIR VEHICLES

public class VehicleFormController {

    @FXML private TextField plateField;
    @FXML private TextField modelField;
    @FXML private TextField colorField;
    @FXML private TextField priceField;
    @FXML private ChoiceBox<String> statusChoiceBox;
    private Vehicle vehicle;
    private Consumer<Vehicle> onSaveCallback;

    @FXML
    public void initialize() {
    }

    // This method is to fill the textfields when a vehicle is selected for easier editing
    // Data is gotten from the Vehicle class
    public void setVehicle(Vehicle vehicle, Consumer<Vehicle> callback) {
        this.vehicle = vehicle;
        this.onSaveCallback = callback;

        if (vehicle != null) {
            plateField.setText(vehicle.getPlate());
            modelField.setText(vehicle.getModel());
            colorField.setText(vehicle.getColor());
            priceField.setText(vehicle.getPrice());
            statusChoiceBox.setValue(vehicle.isAvailability() ? "Available" : "Maintenance");
        }
    }

    // This handler is for updating the vehicle information and stores in onSaveCallBack
    // Error Handling DONE
    @FXML
    private void handleSave() {
        String plate = plateField.getText();
        String model = modelField.getText();
        String color = colorField.getText();
        String price = priceField.getText();
        String status = statusChoiceBox.getValue();

        if (plate.isEmpty() || model.isEmpty() || color.isEmpty() || price.isEmpty() || status == null) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Please fill all fields.");
            return;
        }

        boolean isAvailable = status.equals("Available");

        Vehicle newVehicle = new Vehicle(plate, model, color, price, isAvailable);

        if (onSaveCallback != null) {
            onSaveCallback.accept(newVehicle);
        }

        closeForm();
    }

    // This handler calls the closeForm
    @FXML
    private void handleCancel() {
        closeForm();
    }

    // This method closes the form
    private void closeForm() {
        Stage stage = (Stage) plateField.getScene().getWindow();
        stage.close();
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

    // This method loads the window for the VehicleForm
    // Error Handling DONE
    public static void showVehicleForm(Vehicle vehicle, Consumer<Vehicle> callback) {
        try {
            FXMLLoader loader = new FXMLLoader(VehicleFormController.class.getResource("/com/example/vehicle_rental_management/vehicle_form.fxml"));
            Parent root = loader.load();

            VehicleFormController controller = loader.getController();
            controller.setVehicle(vehicle, callback);

            Stage stage = new Stage();
            stage.setTitle(vehicle == null ? "Add Vehicle" : "Edit Vehicle");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
