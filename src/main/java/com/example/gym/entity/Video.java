package com.example.gym.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    private String url; // Не е задолжително за uploaded videos

    @NotNull
    @Column(name = "order_index")
    private Integer orderIndex;

    @ManyToOne
    @JoinColumn(name = "min_subscription_id")
    private Subscription minSubscription;

    @ManyToOne
    @JoinColumn(name = "day_id")
    @JsonIgnoreProperties("videos")
    private Day day;

    // 🆕 ДОДАДЕНО: Пола за file upload
    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "video_type")
    private String videoType; // 'youtube' или 'upload'

    @Column(name = "mime_type")
    private String mimeType;

    // Constructors
    public Video() {}

    public Video(String title, String description, String url, Integer orderIndex, Day day) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.orderIndex = orderIndex;
        this.day = day;
        this.videoType = "youtube";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }

    public Subscription getMinSubscription() { return minSubscription; }
    public void setMinSubscription(Subscription minSubscription) { this.minSubscription = minSubscription; }

    public Day getDay() { return day; }
    public void setDay(Day day) { this.day = day; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getVideoType() { return videoType; }
    public void setVideoType(String videoType) { this.videoType = videoType; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    // Помошен метод за да го добиеме само dayId
    public Long getDayId() {
        return day != null ? day.getId() : null;
    }

    // Помошен метод за да провериме дали е uploaded video
    public boolean isUploadedVideo() {
        return "upload".equals(videoType) && filePath != null;
    }

    // Помошен метод за да добиеме file URL
    public String getFileUrl() {
        return isUploadedVideo() ? "/api/videos/file/" + id : null;
    }

    @Override
    public String toString() {
        return "Video{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", orderIndex=" + orderIndex +
                ", videoType='" + videoType + '\'' +
                ", fileName='" + fileName + '\'' +
                ", dayId=" + getDayId() +
                '}';
    }
}