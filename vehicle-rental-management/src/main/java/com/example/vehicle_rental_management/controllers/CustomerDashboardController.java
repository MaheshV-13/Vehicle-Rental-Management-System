package com.example.vehicle_rental_management.controllers;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.control.TextInputDialog;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


// THIS IS THE CODE FOR THE CUSTOMER DASHBOARD

public class CustomerDashboardController {
    @FXML private TextField searchField;
    @FXML private ListView<String> vehicleListView;
    @FXML private Button logoutButton;
    @FXML private Label rentedCarLabel;
    private String rentedCarPlate = "";
    private final String VEHICLE_FILE = "Data/vehicles.txt";

    /* Initialize method so that when this window opens it loads the list of vehicles from
       vehicles.txt by calling the loadVehicleData() method */
    @FXML
    private void initialize() {
        loadVehicleData();
    }

    // Reads all vehicle data from vehicles.txt
    // Extract all information in vehicles.txt separated by "," and stores it into and ArrayList
    // Error handling DONE
    private void loadVehicleData() {
        List<String> vehicles = new ArrayList<>();
        File file = new File(VEHICLE_FILE);

        if (!file.exists()) {
            System.out.println("Vehicle file not found!");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String plate = parts[0];
                    String model = parts[1];
                    String color = parts[2];
                    String price = parts[3];
                    boolean available = Boolean.parseBoolean(parts[4]);

                    if (available) {
                        String displayText = plate + " - " + model + " (" + color + ") - " + price;
                        vehicles.add(displayText);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        vehicleListView.getItems().setAll(vehicles);
    }

    // This is the handler for the search & filter bar
    // Reads vehicles.txt and determines whether the vehicle that is searched EXISTS and AVAILABLE
    // If yes, then it displays the vehicle
    // Error handling DONE
    @FXML
    private void handleSearchVehicles() {
        String query = searchField.getText().toLowerCase();
        List<String> filteredVehicles = new ArrayList<>();

        File file = new File(VEHICLE_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String model = parts[1].toLowerCase();
                    String color = parts[2].toLowerCase();
                    boolean available = Boolean.parseBoolean(parts[4]);

                    if (available && (model.contains(query) || color.contains(query))) {
                        String displayText = parts[0] + " - " + parts[1] + " (" + parts[2] + ") - " + parts[3];
                        filteredVehicles.add(displayText);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        vehicleListView.getItems().setAll(filteredVehicles);
    }

    // This handler is used for selecting a vehicle
    // Once selected it will split and trim the data that is separated by " - " and " \\( "
    // This handler consists of additional lines of code for debugging purposes
    // Error handling DONE
    @FXML
    private void handleVehicleSelection() {
        String selectedVehicle = vehicleListView.getSelectionModel().getSelectedItem();
        if (selectedVehicle != null) {
            System.out.println("Selected Vehicle Raw: " + selectedVehicle);

            try {
                String[] vehicleParts = selectedVehicle.split(" - ");
                if (vehicleParts.length == 3) {
                    String plate = vehicleParts[0].trim();
                    String modelAndColor = vehicleParts[1].trim();
                    String price = vehicleParts[2].trim();

                    String[] modelColorParts = modelAndColor.split(" \\(");
                    if (modelColorParts.length == 2) {
                        String model = modelColorParts[0].trim();
                        String color = modelColorParts[1].replace(")", "").trim();
                        System.out.println("Selected Vehicle: Plate: " + plate + ", Model: " + model + ", Color: " + color + ", Price: " + price);
                        openVehicleDetailsWindow(plate, model, color, price);
                    } else {
                        System.out.println("Failed to parse model and color from: " + modelAndColor);
                    }
                } else {
                    System.out.println("Vehicle details format is not as expected.");
                }
            } catch (Exception e) {
                System.out.println("Error in vehicle selection process: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No vehicle selected.");
        }
    }

    // Opens the Vehicle Details window allowing customers to check vehicle details before booking it
    // This handler consists of additional lines of code for debugging purposes
    // Error handling DONE
    private void openVehicleDetailsWindow(String plate, String model, String color, String price) {
        try {
            System.out.println("Attempting to open Vehicle Details window...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vehicle_rental_management/vehicle_details.fxml"));
            Parent root = loader.load();

            VehicleDetailsController controller = loader.getController();
            controller.setVehicleDetails(plate, model, color, price, true);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Vehicle Details");
            stage.show();
            System.out.println("Vehicle Details window opened successfully.");

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error occurred while opening the vehicle details window: " + e.getMessage());
        }
    }


    // This handler creates a TextInputDialog for customers to submit reports/feedback
    // saveReportToFile() method is called to save the reports into reports.txt based on specific customer
    // Error handling DONE
    public void handleReportSubmission() {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Submit Report");
        dialog.setHeaderText("Enter your report:");
        dialog.setContentText("Report:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(reportMessage -> {
            if (!reportMessage.trim().isEmpty()) {
                saveReportToFile(reportMessage);
            } else {
                System.out.println("No message entered.");
            }
        });
    }

    // This method is used to save the reports into reports.txt based on specific customer
    // Gets customer username by calling getCurrentUsername() method
    // Error handling DONE
    private void saveReportToFile(String reportMessage) {
        String username = getCurrentUsername();
        String reportEntry = username + "," + reportMessage + "\n";

        try (FileWriter writer = new FileWriter("Data/reports.txt", true)) {
            writer.write(reportEntry);
            System.out.println("Report saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    // This method is for displaying customer's booking history
    // Gets customer username by calling getCurrentUsername() method
    // Reads booking_confirmation.txt and displays the customer's booking history in a pop-up window
    // Splits the data in booking_confirmation.txt separated by ","
    // Error handling DONE
    @FXML
    private void showBookingHistory() {
        String username = getCurrentUsername();
        StringBuilder history = new StringBuilder("Your Booking History:\n");

        try (BufferedReader reader = new BufferedReader(new FileReader("Data/booking_confirmation.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username)) {
                    history.append("Vehicle: ").append(parts[2])
                            .append(" | Plate: ").append(parts[1])
                            .append(" | Color: ").append(parts[3])
                            .append(" | Paid: RM").append(parts[4]).append("\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading booking history: " + e.getMessage());
        }

        TextArea historyTextArea = new TextArea(history.toString());
        historyTextArea.setEditable(false);
        historyTextArea.setWrapText(true);
        historyTextArea.setPrefWidth(400);
        historyTextArea.setPrefHeight(300);


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Booking History");
        alert.setHeaderText(null);
        alert.getDialogPane().setContent(historyTextArea);
        alert.showAndWait();
    }

    // This is the handler for customers to view the cars they are currently renting
    // Gets the data from rented_cars.txt and split it
    // Error handling DONE
    @FXML
    private void handleViewRentedCar() {
        String username = getCurrentUsername();
        boolean foundRentedCar = false;

        try (BufferedReader reader = new BufferedReader(new FileReader("Data/rented_cars.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username)) {
                    rentedCarPlate = parts[1];
                    String rentedCarModel = parts[2];
                    String rentedCarColor = parts[3];
                    String rentedCarPrice = parts[4];

                    rentedCarLabel.setText("Rented Car: " + rentedCarPlate + " - " + rentedCarModel + " (" + rentedCarColor + ") - RM" + rentedCarPrice);
                    foundRentedCar = true;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!foundRentedCar) {
            rentedCarLabel.setText("No rented car found.");
        }
    }


    // This is the handler for customer to return their car
    // Reads rented_cars.txt and splits the data
    // Stores the data in an ArrayList
    // Opens a new pop-up dialog/window allowing customer to select and return their rented cars
    // Calls the processReturn() method to actually update rented_cars.txt once a car is returned
    // Error handling DONE
    @FXML
    private void handleReturnCar() {
        String username = getCurrentUsername();
        List<String> rentedCars = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("Data/rented_cars.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username)) {
                    rentedCars.add(parts[1] + " - " + parts[2] + " (" + parts[3] + ") - RM" + parts[4]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (rentedCars.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Cars to Return", "You have no rented cars.");
            return;
        }

        ListView<String> listView = new ListView<>();
        listView.getItems().setAll(rentedCars);
        listView.setPrefSize(400, 300);

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Return a Car");
        dialog.setHeaderText("Select a car to return:");
        dialog.getDialogPane().setContent(listView);
        ButtonType returnButton = new ButtonType("Return Car", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(returnButton, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == returnButton) {
                return listView.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(selectedCar -> {
            if (selectedCar != null) {
                String[] carDetails = selectedCar.split(" - ");
                if (carDetails.length >= 2) {
                    String plate = carDetails[0].trim();
                    processReturn(plate, username);
                }
            }
        });
    }

    // This is the method to update the rented_cars.txt once a car is returned
    // Calls updateVehicleAvailability() to change the availability of the car back to true
    // Error handling DONE
    private void processReturn(String plate, String username) {
        File rentedCarsFile = new File("Data/rented_cars.txt");
        List<String> updatedRentedCars = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(rentedCarsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (!(parts[0].equals(username) && parts[1].equals(plate))) {
                    updatedRentedCars.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rentedCarsFile))) {
            for (String car : updatedRentedCars) {
                writer.write(car);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        updateVehicleAvailability(plate, true);
        showAlert(Alert.AlertType.INFORMATION, "Car Returned", "The car has been successfully returned.");
    }

    // This method is used for updating the vehicle availability back to true when a vehicle is returned
    // Reads vehicles.txt and stores the vehicle information based on the number plate of the rented car into an ArrayList
    // Writes back the vehicle information into vehicles.txt after updating the availability
    // Error handling DONE
    private void updateVehicleAvailability(String plate, boolean available) {
        File file = new File(VEHICLE_FILE);
        if (!file.exists()) {
            System.out.println("Vehicle file not found!");
            return;
        }

        List<String> updatedVehicles = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5 && parts[0].equals(plate)) {
                    parts[4] = String.valueOf(available);
                }
                updatedVehicles.add(String.join(",", parts));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String vehicle : updatedVehicles) {
                writer.write(vehicle);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
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
