package com.example.gym.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "videos")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    private String description;

    @NotBlank
    private String url;

    @NotNull
    @Column(name = "order_index")
    private Integer orderIndex;

    @ManyToOne
    @JoinColumn(name = "min_subscription_id")
    private Subscription minSubscription;

    // Constructors
    public Video() {

    }

    public Video(Long id, String title, String description, String url, Integer orderIndex, Subscription minSubscription) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.url = url;
        this.orderIndex = orderIndex;
        this.minSubscription = minSubscription;
    }

    //Geters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Subscription getMinSubscription() {
        return minSubscription;
    }

    public void setMinSubscription(Subscription minSubscription) {
        this.minSubscription = minSubscription;
    }
}
