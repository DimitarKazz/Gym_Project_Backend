package com.example.gym.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "days")
public class Day {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    private String description;

    @NotNull
    @Column(name = "order_index")
    private Integer orderIndex;

    // 🚨 Важно: НЕ JsonIgnore, туку JsonIgnoreProperties("day") за да нема recursion
    @OneToMany(mappedBy = "day", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @JsonIgnoreProperties("day") // кога враќаме Day, од Video ќе се игнорира полето day
    private List<Video> videos = new ArrayList<>();

    // Додадено: Бидирекционална релација со Program
    @ManyToMany(mappedBy = "days")
    @JsonIgnoreProperties("days") // Avoid infinite recursion
    private List<Program> programs = new ArrayList<>();

    // Constructors
    public Day() {}

    public Day(String title, String description, Integer orderIndex) {
        this.title = title;
        this.description = description;
        this.orderIndex = orderIndex;
    }

    // Getters and Setters
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

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    // Додадени getter и setter за programs
    public List<Program> getPrograms() {
        return programs;
    }

    public void setPrograms(List<Program> programs) {
        this.programs = programs;
    }

    // Helper methods for adding/removing videos
    public void addVideo(Video video) {
        videos.add(video);
        video.setDay(this);
    }

    public void removeVideo(Video video) {
        videos.remove(video);
        video.setDay(null);
    }

    @Override
    public String toString() {
        return "Day{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", orderIndex=" + orderIndex +
                '}';
    }

    // equals and hashCode methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Day)) return false;
        Day day = (Day) o;
        return id != null && id.equals(day.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}