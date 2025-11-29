package com.example.gym.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_train_in_gym")
public class UserTrainInGym {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "train_in_gym_id", nullable = false)
    private TrainInGym trainInGym;

    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "remaining_days", nullable = false)
    private Integer remainingDays;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Constructors
    public UserTrainInGym() {}

    public UserTrainInGym(User user, TrainInGym trainInGym) {
        this.user = user;
        this.trainInGym = trainInGym;
        this.purchaseDate = LocalDateTime.now();
        this.remainingDays = trainInGym.getDurationDays();
        this.expiryDate = calculateExpiryDate();
        this.isActive = true;
    }

    // Helper method to calculate expiry date
    private LocalDateTime calculateExpiryDate() {
        return purchaseDate.plusDays(trainInGym.getDurationDays());
    }

    // Method to decrease remaining days
    public void decreaseRemainingDays() {
        if (this.remainingDays > 0) {
            this.remainingDays--;

            // Deactivate if no remaining days
            if (this.remainingDays <= 0) {
                this.isActive = false;
                this.remainingDays = 0;
            }
        }
    }






    // Method to check if training is still valid
    public boolean isValid() {
        return isActive && remainingDays > 0 && LocalDateTime.now().isBefore(expiryDate);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public TrainInGym getTrainInGym() { return trainInGym; }
    public void setTrainInGym(TrainInGym trainInGym) { this.trainInGym = trainInGym; }

    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public Integer getRemainingDays() { return remainingDays; }
    public void setRemainingDays(Integer remainingDays) { this.remainingDays = remainingDays; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}