package com.example.vehicle_rental_management.models;
public class User {
    private String username;
    private String email;
    private String phone;
    private String rentedCar;
    private String carModel;
    private String rentPerDay;
    private String role;

    public User(String username, String email, String phone, String rentedCar, String carModel, String rentPerDay, String role) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.rentedCar = rentedCar;
        this.carModel = carModel;
        this.rentPerDay = rentPerDay;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getRentedCar() {
        return rentedCar;
    }

    public String getCarModel() {
        return carModel;
    }

    public String getRentPerDay() {
        return rentPerDay;
    }

    public String getRole() {
        return role;
    }
}
