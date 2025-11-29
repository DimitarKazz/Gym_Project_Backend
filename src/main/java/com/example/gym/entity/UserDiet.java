// src/main/java/com/example/gym/entity/UserDiet.java
package com.example.gym.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_diets")
public class UserDiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "diet_id", nullable = false)
    private Diet diet;

    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Кориснички податоци
    @Column(name = "height")
    private Double height; // Висина во cm

    @Column(name = "weight")
    private Double weight; // Тежина во kg

    @Column(name = "waist_circumference")
    private Double waistCircumference; // Обем на струк во cm

    @Column(name = "bmi") // 👈 НОВО: BMI поле
    private Double bmi;

    // Constructors
    public UserDiet() {
        this.purchaseDate = LocalDateTime.now();
        this.isActive = true;
    }

    public UserDiet(User user, Diet diet, Double height, Double weight, Double waistCircumference) {
        this.user = user;
        this.diet = diet;
        this.height = height;
        this.weight = weight;
        this.waistCircumference = waistCircumference;
        this.purchaseDate = LocalDateTime.now();
        this.isActive = true;
        this.bmi = calculateBMI(); // 👈 АВТОМАТСКИ ПРЕСМЕТКА
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Diet getDiet() {
        return diet;
    }

    public void setDiet(Diet diet) {
        this.diet = diet;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
        this.bmi = calculateBMI(); // 👈 АВТОМАТСКА ПРЕСМЕТКА
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
        this.bmi = calculateBMI(); // 👈 АВТОМАТСКА ПРЕСМЕТКА
    }

    public Double getWaistCircumference() {
        return waistCircumference;
    }

    public void setWaistCircumference(Double waistCircumference) {
        this.waistCircumference = waistCircumference;
    }

    public Double getBmi() {
        return bmi;
    }

    public void setBmi(Double bmi) {
        this.bmi = bmi;
    }

    // 👇 ПОДОБРЕНА: Helper method за BMI калкулација
    public Double calculateBMI() {
        if (height != null && weight != null && height > 0) {
            double heightInMeters = height / 100.0;
            double calculatedBMI = weight / (heightInMeters * heightInMeters);
            return Math.round(calculatedBMI * 100.0) / 100.0; // Zaokruzi na 2 decimali
        }
        return null;
    }

    // 👇 НОВО: Helper method за BMI категорија
    public String getBMICategory() {
        if (bmi == null) return "N/A";

        if (bmi < 18.5) return "Недоволна тежина";
        if (bmi < 25) return "Нормална тежина";
        if (bmi < 30) return "Прекумерна тежина";
        if (bmi < 35) return "Дебелина (степен 1)";
        if (bmi < 40) return "Дебелина (степен 2)";
        return "Дебелина (степен 3)";
    }

    // 👇 НОВО: Helper method за препорачана тежина
    public String getRecommendedWeightRange() {
        if (height == null || height <= 0) return "N/A";

        double heightInMeters = height / 100.0;
        double minWeight = 18.5 * heightInMeters * heightInMeters;
        double maxWeight = 24.9 * heightInMeters * heightInMeters;

        return String.format("%.1f - %.1f kg", minWeight, maxWeight);
    }

    // 👇 НОВО: Lifecycle callback за автоматска пресметка пред зачувување
    @PrePersist
    @PreUpdate
    private void calculateAndSetBMI() {
        this.bmi = calculateBMI();
    }
}