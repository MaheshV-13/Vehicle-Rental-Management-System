package com.example.vehicle_rental_management.controllers;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.*;

// THIS IS THE CODE TO ALLOW USERS TO CHANGE PASSWORD

public class ChangePasswordController {
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmNewPasswordField;
    @FXML private Label errorLabel;
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    // This is the handler for forgot password button
    // Opens a pop-up windows prompting user to enter new password
    // Also updates the password with hashed encryption into users.txt
    // Error handling DONE
    @FXML
    private void handleChangePassword() {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmNewPasswordField.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("Please enter both new password fields.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }

        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

        try {
            File userFile = new File("Data/users.txt");
            BufferedReader reader = new BufferedReader(new FileReader(userFile));
            StringBuilder fileContent = new StringBuilder();
            String line;
            boolean passwordUpdated = false;

            while ((line = reader.readLine()) != null) {
                String[] user = line.split(",");
                if (user[0].equals(username)) {
                    fileContent.append(username).append(",").append(hashedPassword).append(",").append(user[2]).append("\n");
                    passwordUpdated = true;
                } else {
                    fileContent.append(line).append("\n");
                }
            }

            reader.close();

            if (passwordUpdated) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(userFile));
                writer.write(fileContent.toString());
                writer.close();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Password Change");
                alert.setHeaderText("Password Updated Successfully");
                alert.setContentText("Your password has been updated.");
                alert.showAndWait();

                Stage stage = (Stage) newPasswordField.getScene().getWindow();
                stage.close();
            } else {
                errorLabel.setText("Error updating password.");
            }
        } catch (IOException e) {
            errorLabel.setText("Error updating password: " + e.getMessage());
        }
    }

    // This is the handler to cancel forgot password
    // Closes the pop-up window and redirects back to login window
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) newPasswordField.getScene().getWindow();
        stage.close();
    }
}
