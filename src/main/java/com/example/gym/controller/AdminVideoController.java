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

    @PostMapping
    public ResponseEntity<Video> createVideo(@RequestBody Video video) {
        Video createdVideo = videoService.createVideo(video);
        return ResponseEntity.ok(createdVideo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Video> updateVideo(@PathVariable Long id, @RequestBody Video videoDetails) {
        try {
            Video updatedVideo = videoService.updateVideo(id, videoDetails);
            return ResponseEntity.ok(updatedVideo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        try {
            videoService.deleteVideo(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
