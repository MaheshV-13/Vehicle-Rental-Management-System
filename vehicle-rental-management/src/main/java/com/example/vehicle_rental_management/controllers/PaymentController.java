package com.example.vehicle_rental_management.controllers;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.*;
import java.util.*;


// THIS IS THE CODE FOR THE PAYMENT PAGE

public class PaymentController {

    @FXML
    private Label totalAmountLabel;
    private double totalPrice;
    private String vehiclePlate;
    private String vehicleModel;
    private String vehicleColor;

    // This is the method to set all the respective details based on the Vehicle class
    // and display the final price
    public void setPaymentDetails(double totalPrice, String plate, String model, String color) {
        this.totalPrice = totalPrice;
        this.vehiclePlate = plate;
        this.vehicleModel = model;
        this.vehicleColor = color;
        totalAmountLabel.setText("Total: RM" + totalPrice);
    }

    // This is the handler for the confirm button
    // Confirms the payment
    // Calls the saveBookingConfirmation() method for to save the vehicle booking details for reference
    // Calls the updateVehicleAvailability() to change the vehicle availability to false
    // Closes the window
    @FXML
    private void handleConfirmPayment() {
        saveBookingConfirmation();
        updateVehicleAvailability();
        showAlert(Alert.AlertType.INFORMATION, "Payment Successful", "Your booking has been confirmed!");

        Stage stage = (Stage) totalAmountLabel.getScene().getWindow();
        stage.close();
    }

    // This is the handler for the cancel payment button
    @FXML
    private void handleCancelPayment() {
        showAlert(Alert.AlertType.INFORMATION, "Payment Cancelled", "Your booking has been cancelled.");
        Stage stage = (Stage) totalAmountLabel.getScene().getWindow();
        stage.close();
    }

    // This is the method to save/write the booking details into booking_confirmation.txt and rented_cars.txt
    // Error Handling DONE
    private void saveBookingConfirmation() {
        String username = getCurrentUsername();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Data/booking_confirmation.txt", true))) {
            writer.write(username + "," + vehiclePlate + "," + vehicleModel + "," + vehicleColor + "," + totalPrice);
            writer.newLine();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "File Error", "Failed to save booking confirmation.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Data/rented_cars.txt", true))) {
            writer.write(username + "," + vehiclePlate + "," + vehicleModel + "," + vehicleColor + "," + totalPrice);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // This method is used for updating the vehicle availability back to true when a vehicle is returned
    // Reads vehicles.txt and stores the vehicle information based on the number plate of the rented car into an ArrayList
    // Writes back the vehicle information into vehicles.txt after updating the availability
    // Error handling DONE
    private void updateVehicleAvailability() {
        List<String> updatedVehicles = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("Data/vehicles.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] vehicleData = line.split(",");
                if (vehicleData[0].equals(vehiclePlate)) {
                    vehicleData[4] = "false";
                }
                updatedVehicles.add(String.join(",", vehicleData));
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("Data/vehicles.txt"))) {
                for (String updatedVehicle : updatedVehicles) {
                    writer.write(updatedVehicle);
                    writer.newLine();
                }
            }

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "File Error", "Failed to update vehicle availability.");
        }
    }

    // This is to get the current username of the customer that is logged in for efficient organization of data
    private String getCurrentUsername() {
        return LoginController.loggedInUsername;
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
