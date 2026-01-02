package com.example.vehicle_rental_management.controllers;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


// THIS IS THE CODE FOR THE REGISTRATION PAGE

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField securityAnswerField;
    @FXML private Label errorLabel;

    // This is the initialize method to make sure the combobox content is correct when the window/stage opens
    @FXML
    private void initialize() {
        roleComboBox.getItems().clear();
        roleComboBox.getItems().addAll("Customer", "Vehicle Owner");
    }

    // This is the handler for registration
    // Gets all the user information including security questions
    // For encryption and users privacy, the password is hashed when saved to users.txt and userdetails.txt
    // users.txt is only the username, password and role
    // userdetails.txt is all the details
    // This is done to ensure the processing of details is faster and ensure better scalability.
    // Once completed, it redirects back to login screen
    // Error Handling DONE
    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String role = roleComboBox.getValue();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String securityAnswer = securityAnswerField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty() || phone.isEmpty() || securityAnswer.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        if (role == null || role.isEmpty()) {
            errorLabel.setText("Please select a role.");
            return;
        }

        if (!isValidEmail(email)) {
            errorLabel.setText("Invalid email format.");
            return;
        }

        if (!isValidPhoneNumber(phone)) {
            errorLabel.setText("Invalid phone number format.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }

        String passwordError = getPasswordStrengthError(password);
        if (passwordError != null) {
            errorLabel.setText(passwordError);
            return;
        }

        File userFile = new File("Data/users.txt");
        try {
            if (userFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(userFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] userData = line.split(",");
                    if (userData.length >= 3) {
                        String existingUsername = userData[0];
                        String existingRole = userData[2];
                        if (existingUsername.equals(username) && existingRole.equals(role)) {
                            errorLabel.setText("An account with this username and role already exists.");
                            reader.close();
                            return;
                        }
                    }
                }
                reader.close();
            }
        } catch (IOException e) {
            errorLabel.setText("Error reading user file: " + e.getMessage());
            return;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(userFile, true));
            writer.write(username + "," + hashedPassword + "," + role);
            writer.newLine();
            writer.close();

            File detailsFile = new File("Data/userdetails.txt");
            BufferedWriter detailsWriter = new BufferedWriter(new FileWriter(detailsFile, true));
            detailsWriter.write(username + "," + email + "," + phone);
            detailsWriter.newLine();
            detailsWriter.close();

            File secFile = new File("Data/security_questions.txt");
            BufferedWriter secWriter = new BufferedWriter(new FileWriter(secFile, true));
            secWriter.write(username + "," + securityAnswer);
            secWriter.newLine();
            secWriter.close();

            errorLabel.setText("Registration successful!");
            handleBackToLogin();
        } catch (IOException e) {
            errorLabel.setText("Error saving user: " + e.getMessage());
        }
    }

    // This is the handler to redirect user back to the loading page
    // Error Handling DONE
    @FXML
    private void handleBackToLogin() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vehicle_rental_management/login.fxml"));
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            errorLabel.setText("Error loading login page: " + e.getMessage());
        }
    }

    // This method is for email error handling
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // This method is for phone number error handling
    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("01[0-9]{8}");
    }

    private String getPasswordStrengthError(String password) {
        if (password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }
        if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter.";
        }
        if (!password.matches(".*[a-z].*")) {
            return "Password must contain at least one lowercase letter.";
        }
        if (!password.matches(".*\\d.*")) {
            return "Password must contain at least one digit.";
        }
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            return "Password must contain at least one special character (!@#$%^&* etc.).";
        }
        return null;
    }
}
