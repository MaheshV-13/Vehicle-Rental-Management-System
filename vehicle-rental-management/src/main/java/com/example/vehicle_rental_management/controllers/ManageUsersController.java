package com.example.vehicle_rental_management.controllers;
import com.example.vehicle_rental_management.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import java.io.*;
import java.util.*;


//THIS CODE IS FOR THE MANAGE USER PAGE FOR ADMINS TO MANAGE USERS

public class ManageUsersController {

    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> phoneColumn;
    @FXML
    private TableColumn<User, String> rentedCarColumn;
    @FXML
    private TableColumn<User, String> carModelColumn;
    @FXML
    private TableColumn<User, String> rentPerDayColumn;
    @FXML
    private TableColumn<User, String> roleColumn;
    private List<User> userList = new ArrayList<>();

    // This method loads the users from userdetails.txt
    // It will get all the necessary details such as rented car, model, rent, role, etc..
    // by calling each of the respective methods
    // Store the details into an ArrayList and fills up the table on the manage user window with the details
    // Error Handling DONE
    public void loadUsers() {
        userList.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader("Data/userdetails.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    String username = data[0].trim();
                    String email = data[1].trim();
                    String phone = data[2].trim();
                    String rentedCar = getRentedCar(username);
                    String carModel = getCarModel(rentedCar);
                    String rentPerDay = getRentPerDay(rentedCar);
                    String role = getRole(username);

                    User user = new User(username, email, phone, rentedCar, carModel, rentPerDay, role);
                    userList.add(user);
                }
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load user data.");
        }

        usernameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        phoneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
        rentedCarColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRentedCar()));
        carModelColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCarModel()));
        rentPerDayColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRentPerDay()));
        roleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));  // Set role column

        usersTable.getItems().setAll(userList);
    }

    // This is the method to get the rented car from rented_cars.txt based on the username
    // Error Handling DONE
    private String getRentedCar(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader("Data/rented_cars.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 2 && data[0].trim().equals(username)) {
                    return data[1].trim();
                }
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load rented cars.");
        }
        return "No car rented";
    }

    // This is the method to get the car model from vehicles.txt based on the rented car
    // Error Handling DONE
    private String getCarModel(String rentedCar) {
        try (BufferedReader reader = new BufferedReader(new FileReader("Data/vehicles.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 3 && data[0].trim().equals(rentedCar)) {
                    return data[1].trim();
                }
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load vehicle details.");
        }
        return "Unknown Model";
    }

    // This is the method to get the rent per day from vehicles.txt based on the rented car
    // Error Handling DONE
    private String getRentPerDay(String rentedCar) {
        try (BufferedReader reader = new BufferedReader(new FileReader("Data/vehicles.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 4 && data[0].trim().equals(rentedCar)) {
                    return data[3].trim();  // Return rent per day with RM
                }
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load vehicle details.");
        }
        return "RM 0.00";
    }

    // This is the method to get the role of users from users.txt based on the username
    // Error Handling DONE
    private String getRole(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader("Data/users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 3 && data[0].trim().equals(username)) {
                    return data[2].trim();  // Return the role of the user
                }
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load user roles.");
        }
        return "Unknown Role";
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
