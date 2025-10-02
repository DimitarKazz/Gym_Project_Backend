package com.example.gym.service;

import com.example.gym.entity.Day;
import com.example.gym.entity.Video;
import com.example.gym.repository.DayRepository;
import com.example.gym.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private DayRepository dayRepository;

    public List<Video> getAllVideos() {
        return videoRepository.findAllByOrderByOrderIndexAsc();
    }

    public List<Video> getVideosByDay(Long dayId) {
        return videoRepository.findByDay_IdOrderByOrderIndexAsc(dayId);
    }

    public Video getVideoById(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video not found with id " + id));
    }

    public Video createVideo(Video video) {
        // Автоматски додели редослед ако не е поставен
        if (video.getOrderIndex() == null) {
            List<Video> videos = getAllVideos();
            int maxOrder = videos.stream()
                    .mapToInt(Video::getOrderIndex)
                    .max()
                    .orElse(0);
            video.setOrderIndex(maxOrder + 1);
        }

        // Ако видеото има ден, осигурај се дека денот постои
        if (video.getDay() != null && video.getDay().getId() != null) {
            Day day = dayRepository.findById(video.getDay().getId())
                    .orElseThrow(() -> new RuntimeException("Day not found with id: " + video.getDay().getId()));
            video.setDay(day);
        }

        return videoRepository.save(video);
    }

    // 🆕 ДОДАДЕНО: Create video with day ID
    public Video createVideoWithDay(Video video, Long dayId) {
        Day day = dayRepository.findById(dayId)
                .orElseThrow(() -> new RuntimeException("Day not found with id: " + dayId));
        video.setDay(day);

        // Автоматски додели редослед ако не е поставен
        if (video.getOrderIndex() == null) {
            List<Video> dayVideos = getVideosByDay(dayId);
            int maxOrder = dayVideos.stream()
                    .mapToInt(Video::getOrderIndex)
                    .max()
                    .orElse(0);
            video.setOrderIndex(maxOrder + 1);
        }

        return videoRepository.save(video);
    }

    public Video updateVideo(Long id, Video videoDetails) {
        Video video = getVideoById(id);

        video.setTitle(videoDetails.getTitle());
        video.setDescription(videoDetails.getDescription());
        video.setUrl(videoDetails.getUrl());
        video.setOrderIndex(videoDetails.getOrderIndex());
        video.setMinSubscription(videoDetails.getMinSubscription());

        // 🆕 ДОДАДЕНО: Ажурирај ги file полата
        if (videoDetails.getFileName() != null) {
            video.setFileName(videoDetails.getFileName());
        }
        if (videoDetails.getFileSize() != null) {
            video.setFileSize(videoDetails.getFileSize());
        }
        if (videoDetails.getFilePath() != null) {
            video.setFilePath(videoDetails.getFilePath());
        }
        if (videoDetails.getVideoType() != null) {
            video.setVideoType(videoDetails.getVideoType());
        }
        if (videoDetails.getMimeType() != null) {
            video.setMimeType(videoDetails.getMimeType());
        }

        return videoRepository.save(video);
    }

    public void deleteVideo(Long id) {
        Video video = getVideoById(id);
        videoRepository.delete(video);
    }

    // ✅ Реorder видеа
    public List<Video> reorderVideos(List<Long> videoIds) {
        int index = 1;
        for (Long videoId : videoIds) {
            Video video = getVideoById(videoId);
            video.setOrderIndex(index++);
            videoRepository.save(video);
        }
        return videoRepository.findAllByOrderByOrderIndexAsc();
    }

    public Video updateVideoOrder(Long id, Integer newOrder) {
        Video video = getVideoById(id);
        video.setOrderIndex(newOrder);
        return videoRepository.save(video);
    }
}