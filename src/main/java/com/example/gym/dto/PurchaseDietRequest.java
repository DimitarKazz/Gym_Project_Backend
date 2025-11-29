// src/main/java/com/example/gym/dto/PurchaseDietRequest.java
package com.example.gym.dto;

public class PurchaseDietRequest {
    private Double height;
    private Double weight;
    private Double waistCircumference;

    // Constructors
    public PurchaseDietRequest() {}

    public PurchaseDietRequest(Double height, Double weight, Double waistCircumference) {
        this.height = height;
        this.weight = weight;
        this.waistCircumference = waistCircumference;
    }

    // Getters and Setters
    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getWaistCircumference() {
        return waistCircumference;
    }

    public void setWaistCircumference(Double waistCircumference) {
        this.waistCircumference = waistCircumference;
    }
}