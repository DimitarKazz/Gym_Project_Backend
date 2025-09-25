package com.example.gym.controller;
import com.example.gym.entity.Video;
import com.example.gym.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping
    public ResponseEntity<List<Video>>getAllVideos()
    {
        List<Video> videos = videoService.getAllVideos();
        return ResponseEntity.ok(videos);

    }
}
