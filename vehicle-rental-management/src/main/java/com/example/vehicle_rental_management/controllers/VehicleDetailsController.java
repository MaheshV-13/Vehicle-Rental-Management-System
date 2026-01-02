package com.example.vehicle_rental_management.controllers;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.*;
import java.util.Optional;


// THIS IS THE CODE FOR CUSTOMER TO CHECK THE VEHICLE DETAILS BEFORE BOOKING

public class VehicleDetailsController {

    @FXML
    private Label vehiclePlateLabel;
    @FXML
    private Label vehicleModelLabel;
    @FXML
    private Label vehicleColorLabel;
    @FXML
    private Label vehiclePriceLabel;
    @FXML
    private Label vehicleAvailabilityLabel;
    @FXML
    private Button bookNowButton;
    private String vehiclePlate;
    private String vehicleModel;
    private String vehicleColor;
    private String vehiclePrice;
    private boolean vehicleAvailable;

    // This method is used to get all the vehicle details from the Vehicle class
    // Sets and displays the details on the window
    public void setVehicleDetails(String plate, String model, String color, String price, boolean available) {
        vehiclePlate = plate;
        vehicleModel = model;
        vehicleColor = color;
        vehiclePrice = price;
        vehicleAvailable = available;

        vehiclePlateLabel.setText("Plate: " + vehiclePlate);
        vehicleModelLabel.setText("Model: " + vehicleModel);
        vehicleColorLabel.setText("Color: " + vehicleColor);
        vehiclePriceLabel.setText("Price per Day: " + vehiclePrice);
        vehicleAvailabilityLabel.setText("Availability: " + (vehicleAvailable ? "Available" : "Not Available"));
    }

    // This is the handler for the back button to redirect customer back to their dashboard
    @FXML
    private void handleBack() {
        Stage stage = (Stage) bookNowButton.getScene().getWindow();
        stage.close();
    }


    // This is the handler for the book now button
    // Opens a TextInputDialog
    // Calculates the price of the rent based on rental duration
    // Calls openPaymentPage() to proceed with payment
    // Error Handling DONE
    @FXML
    private void handleBookNow() {
        if (vehicleAvailable) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Rental Duration");
            dialog.setHeaderText("Enter rental duration in hours:");
            dialog.setContentText("Hours:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(durationStr -> {
                try {
                    double duration = Double.parseDouble(durationStr);

                    String cleanedPrice = vehiclePrice.replace("RM", "").trim();
                    double dailyRate = Double.parseDouble(cleanedPrice);

                    double totalPrice = (duration >= 24) ? (dailyRate * (duration / 24)) : ((dailyRate / 24) * duration);
                    totalPrice = Math.round(totalPrice * 100.0) / 100.0;

                    openPaymentPage(totalPrice);

                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for the rental duration.");
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "Unavailable", "This vehicle is not available.");
        }
    }

    // This method loads the payment page and passes the data to PaymentController
    // Error Handling DONE
    private void openPaymentPage(double totalPrice) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vehicle_rental_management/payment.fxml"));
            Stage paymentStage = new Stage();
            Scene scene = new Scene(loader.load());

            PaymentController paymentController = loader.getController();
            paymentController.setPaymentDetails(totalPrice, vehiclePlate, vehicleModel, vehicleColor);

            paymentStage.setTitle("Payment");
            paymentStage.setScene(scene);
            paymentStage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load the payment page.");
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
