package com.example.vehicle_rental_management.controllers;
import com.example.vehicle_rental_management.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


// THIS IS THE CODE FOR THE LOADING SCREEN

// This is just a method to create a delay before the login screen is loaded
public class SplashScreenController {
    @FXML
    public void initialize() {
        new Thread(() -> {
            try {
                Thread.sleep(3000);

                Platform.runLater(() -> {
                    try {
                        Stage stage = (Stage) Main.getPrimaryStage();

                        Parent root = FXMLLoader.load(getClass().getResource("/com/example/vehicle_rental_management/login.fxml"));
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
