package com.example.vehicle_rental_management.models;
public class Vehicle {
    private String plate;
    private String model;
    private String color;
    private String price;
    private boolean availability;

    public Vehicle(String plate, String model, String color, String price, boolean availability) {
        this.plate = plate;
        this.model = model;
        this.color = color;
        this.price = price;
        this.availability = availability;
    }

    public String getPlate() {
        return plate;
    }

    public String getModel() {
        return model;
    }

    public String getColor() {
        return color;
    }

    public String getPrice() {
        return price;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }
}

