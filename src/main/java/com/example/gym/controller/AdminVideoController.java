package com.example.gym.controller;

import com.example.gym.entity.Video;
import com.example.gym.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/videos")
public class AdminVideoController {

    @Autowired
    private VideoService videoService;

    // 1. Добиј ги сите видеа (за админ панел)
    @GetMapping
    public ResponseEntity<List<Video>> getAllVideos() {
        List<Video> videos = videoService.getAllVideos();
        return ResponseEntity.ok(videos);
    }

    // 2. Добиј конкретно видео по ID
    @GetMapping("/{id}")
    public ResponseEntity<Video> getVideoById(@PathVariable Long id) {
        try {
            Video video = videoService.getVideoById(id);
            return ResponseEntity.ok(video);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 3. Креирај ново видео
    @PostMapping
    public ResponseEntity<Video> createVideo(@RequestBody Video video) {
        try {
            Video createdVideo = videoService.createVideo(video);
            return ResponseEntity.ok(createdVideo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 4. Ажурирај постоечко видео
    @PutMapping("/{id}")
    public ResponseEntity<Video> updateVideo(@PathVariable Long id, @RequestBody Video videoDetails) {
        try {
            Video updatedVideo = videoService.updateVideo(id, videoDetails);
            return ResponseEntity.ok(updatedVideo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 5. Избриши видео
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        try {
            videoService.deleteVideo(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 6. Промени редослед на видеа
    @PutMapping("/reorder")
    public ResponseEntity<List<Video>> reorderVideos(@RequestBody List<Long> videoIds) {
        try {
            List<Video> reorderedVideos = videoService.reorderVideos(videoIds);
            return ResponseEntity.ok(reorderedVideos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 7. Додај видео на конкретна позиција
    @PutMapping("/{id}/order/{newOrder}")
    public ResponseEntity<Video> updateVideoOrder(@PathVariable Long id, @PathVariable Integer newOrder) {
        try {
            Video updatedVideo = videoService.updateVideoOrder(id, newOrder);
            return ResponseEntity.ok(updatedVideo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}