package com.example.gym.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @NotBlank
    private String name;

    @NotNull
    @DecimalMin(value = "0.0",inclusive = false)
    private BigDecimal price;

    @NotNull
    @Column(name = "duration_days")
    private Integer durationDays;
    //Constructors

    public Subscription() {
    }

    public Subscription(Long id, String name, BigDecimal price, Integer durationDays) {
        Id = id;
        this.name = name;
        this.price = price;
        this.durationDays = durationDays;
    }
    //Getters and Setters


    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }
}
