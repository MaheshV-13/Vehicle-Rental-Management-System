module com.example.vehicle_rental_management {
    requires javafx.controls;
    requires javafx.fxml;
    requires jbcrypt;

    opens com.example.vehicle_rental_management.controllers to javafx.fxml;
    exports com.example.vehicle_rental_management;
    opens com.example.vehicle_rental_management.models to javafx.fxml;
}