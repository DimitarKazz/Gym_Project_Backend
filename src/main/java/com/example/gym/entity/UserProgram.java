package com.example.gym.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_programs")
public class UserProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER) // 👈 Додадов EAGER за да ги вчитува податоците
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER) // 👈 Додадов EAGER
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "remaining_days", nullable = false)
    private Integer remainingDays;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Constructors
    public UserProgram() {}

    public UserProgram(User user, Program program) {
        this.user = user;
        this.program = program;
        this.purchaseDate = LocalDateTime.now();
        this.remainingDays = program.getDurationDays();
        this.expiryDate = calculateExpiryDate();
        this.isActive = true;
    }

    // Helper method to calculate expiry date
    private LocalDateTime calculateExpiryDate() {
        return purchaseDate.plusDays(program.getDurationDays());
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

    // Method to check if program is still valid
    public boolean isValid() {
        return isActive && remainingDays > 0 && LocalDateTime.now().isBefore(expiryDate);
    }

    // 👇 НОВО: Автоматска проверка пред секој пристап
    @PreUpdate
    @PrePersist
    private void checkValidity() {
        // Ако поминат сите денови, деактивирај
        if (LocalDateTime.now().isAfter(expiryDate)) {
            this.isActive = false;
            this.remainingDays = 0;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Program getProgram() { return program; }
    public void setProgram(Program program) { this.program = program; }

    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public Integer getRemainingDays() { return remainingDays; }
    public void setRemainingDays(Integer remainingDays) { this.remainingDays = remainingDays; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}