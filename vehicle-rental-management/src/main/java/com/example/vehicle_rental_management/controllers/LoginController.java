package com.example.vehicle_rental_management.controllers;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import java.io.*;


// THIS IS THE CODE FOR THE LOGIN MENU

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    public static String loggedInUsername = "";

    // This is the handler for logging into the app
    // Navigates user based on their role to their respective dashboard by calling the navigateToDashboard() & isAdmin method
    // Error handling DONE
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            return;
        }

        try {
            if (isAdmin(username, password)) {
                navigateToDashboard("Admin");
            } else {
                File userFile = new File("Data/users.txt");
                BufferedReader reader = new BufferedReader(new FileReader(userFile));
                String line;
                boolean loginSuccessful = false;

                while ((line = reader.readLine()) != null) {
                    String[] user = line.split(",");
                    String storedUsername = user[0];
                    String storedPassword = user[1];
                    String storedRole = user[2];

                    if (storedUsername.equals(username) && BCrypt.checkpw(password, storedPassword)) {
                        loggedInUsername = username;
                        loginSuccessful = true;
                        navigateToDashboard(storedRole);
                        break;
                    }
                }

                if (!loginSuccessful) {
                    errorLabel.setText("Invalid username or password.");
                }

                reader.close();
            }
        } catch (IOException e) {
            errorLabel.setText("Error reading user data: " + e.getMessage());
        }
    }

    // This is the method to check whether the user is an admin or not
    // Reads and compare the username and password in admins.txt
    private boolean isAdmin(String username, String password) throws IOException {
        File adminFile = new File("Data/admins.txt");
        BufferedReader reader = new BufferedReader(new FileReader(adminFile));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] admin = line.split(",");
            String adminUsername = admin[0];
            String adminPassword = admin[1];

            if (adminUsername.equals(username) && BCrypt.checkpw(password, adminPassword)) {
                return true;
            }
        }
        reader.close();
        return false;
    }

    // This method is for role-based dashboard navigation
    // Error handling DONE
    private void navigateToDashboard(String role) {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader loader;
            String fxmlFile = switch (role) {
                case "Admin" -> "/com/example/vehicle_rental_management/admin_dashboard.fxml";
                case "Customer" -> "/com/example/vehicle_rental_management/customer_dashboard.fxml";
                case "Vehicle Owner" -> "/com/example/vehicle_rental_management/vehicle_owner_dashboard.fxml";
                default -> "";
            };

            loader = new FXMLLoader(getClass().getResource(fxmlFile));
            stage.setScene(new Scene(loader.load()));


        } catch (IOException e) {
            System.out.println("Error loading dashboard: " + e.getMessage());
        }
    }

    // This handler is for user to change their password
    // For security purposes, users are required to answer a security question to proceed
    // If the answer does not match the actual answer of the user in security_questions.txt it will say incorrect
    // If correct, openPasswordChangeWindow() is called and user will be redirected to a new window
    // Error Handling DONE
    @FXML
    private void handleForgotPassword() {
        String username = usernameField.getText();

        if (username.isEmpty()) {
            errorLabel.setText("Please enter your username.");
            return;
        }

        try {
            File secFile = new File("Data/security_questions.txt");
            BufferedReader reader = new BufferedReader(new FileReader(secFile));
            String line;
            boolean userFound = false;

            while ((line = reader.readLine()) != null) {
                String[] details = line.split(",");
                String storedUsername = details[0];
                String storedAnswer = details[1];

                if (storedUsername.equals(username)) {
                    userFound = true;
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setHeaderText("Security Question: What is your favorite color?");
                    dialog.setContentText("Answer: ");
                    dialog.showAndWait().ifPresent(answer -> {
                        if (answer.equals(storedAnswer)) {
                            openPasswordChangeWindow(username);
                        } else {
                            errorLabel.setText("Incorrect answer.");
                        }
                    });
                    break;
                }
            }

            if (!userFound) {
                errorLabel.setText("No account found with that username.");
            }

            reader.close();
        } catch (IOException e) {
            errorLabel.setText("Error reading security question data: " + e.getMessage());
        }
    }

    // This method will redirect the user to the Change Password Window
    // Error Handling DONE
    private void openPasswordChangeWindow(String username) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vehicle_rental_management/change_password.fxml"));
            Scene scene = new Scene(loader.load());

            ChangePasswordController controller = loader.getController();
            controller.setUsername(username);  // Pass the username to the new controller
            stage.setScene(scene);
            stage.setTitle("Change Password");
            stage.show();
        } catch (IOException e) {
            errorLabel.setText("Error loading password change window: " + e.getMessage());
        }
    }

    // This method is to redirect user to the registration page if they want to create an account
    // Error Handling DONE
    @FXML
    private void handleRegister() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vehicle_rental_management/register.fxml"));
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            System.out.println("Error loading register page: " + e.getMessage());
        }
    }
}
