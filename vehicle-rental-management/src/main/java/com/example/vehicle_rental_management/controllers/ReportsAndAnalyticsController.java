package com.example.vehicle_rental_management.controllers;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.*;
import java.util.*;


// THIS IS THE CODE FOR THE REPORTS AND ANALYTICS PAGE FOR ADMINS TO VIEW CUSTOMER FEEDBACKS

public class ReportsAndAnalyticsController {

    @FXML
    private Label mostRentedCarLabel;
    @FXML
    private Label leastRentedCarLabel;
    @FXML
    private Label totalRevenueLabel;
    @FXML
    private ListView<String> feedbackListView;
    private Map<String, Integer> carRentalCountMap = new HashMap<>();
    private double totalRevenue = 0.0;

    // This method calls loadRentalData() and loadCustomerFeedback()
    public void loadReportsAndAnalytics() {
        loadRentalData();
        loadCustomerFeedback();
    }

    // This method reads booking_confirmation.txt and get the rent per day
    // Calculates the total revenue based on how many cars have been rented so far
    // Also displays the most and least rented car
    // Error Handling DONE
    private void loadRentalData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("Data/booking_confirmation.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 5) {
                    try {
                        double rentalRevenue = Double.parseDouble(data[4].trim());
                        totalRevenue += rentalRevenue;
                    } catch (NumberFormatException e) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Invalid revenue value in booking confirmation.");
                    }

                    String rentedCar = data[2].trim();
                    carRentalCountMap.put(rentedCar, carRentalCountMap.getOrDefault(rentedCar, 0) + 1);
                }
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load rental data.");
        }

        String mostRentedCar = Collections.max(carRentalCountMap.entrySet(), Map.Entry.comparingByValue()).getKey();
        String leastRentedCar = Collections.min(carRentalCountMap.entrySet(), Map.Entry.comparingByValue()).getKey();
        mostRentedCarLabel.setText("Most Rented Car: " + mostRentedCar);
        leastRentedCarLabel.setText("Least Rented Car: " + leastRentedCar);
        totalRevenueLabel.setText("Total Revenue: RM " + String.format("%.2f", totalRevenue));
    }

    // This method reads customer feedback from reports.txt and displays it
    // Error Handling DONE
    private void loadCustomerFeedback() {
        try (BufferedReader reader = new BufferedReader(new FileReader("Data/reports.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2) {
                    String feedback = data[1].trim();
                    feedbackListView.getItems().add(feedback);
                }
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load customer feedback.");
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
