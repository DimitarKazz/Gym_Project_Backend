package com.example.gym.controller;

import com.example.gym.entity.Video;
import com.example.gym.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin(origins = "http://localhost:3000")
public class VideoController {

    @Autowired
    private VideoService videoService;

    private final String UPLOAD_DIR = "uploads/videos/";
    private final long MAX_FILE_SIZE = 1024 * 1024 * 1024; // 1GB
    private final String BASE_URL = "http://localhost:8080"; // Променете ако е друг сервер

    // --- GET сите видеа ---
    @GetMapping
    public ResponseEntity<List<Video>> getAllVideos() {
        try {
            List<Video> videos = videoService.getAllVideos();
            videos.forEach(v -> {
                if (v.isUploadedVideo()) v.setUrl(BASE_URL + v.getFileUrl());
            });
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // --- GET видеа по ден ---
    @GetMapping("/day/{dayId}")
    public ResponseEntity<List<Video>> getVideosByDay(@PathVariable Long dayId) {
        try {
            List<Video> videos = videoService.getVideosByDay(dayId);
            videos.forEach(v -> {
                if (v.isUploadedVideo()) v.setUrl(BASE_URL + v.getFileUrl());
            });
            return ResponseEntity.ok(videos);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // --- POST upload видео ---
    @PostMapping("/upload")
    public ResponseEntity<Video> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "orderIndex", defaultValue = "1") Integer orderIndex,
            @RequestParam(value = "dayId", required = false) Long dayId
    ) {
        try {
            // Проверка големина
            if (file.getSize() > MAX_FILE_SIZE || !file.getContentType().startsWith("video/")) {
                return ResponseEntity.badRequest().build();
            }

            // Создавање директориум
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            // Генерирање уникатно име
            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID() + fileExtension;
            Path filePath = uploadPath.resolve(uniqueFileName);

            // Зачувување фајл
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Креирање Video објект
            Video video = new Video();
            video.setTitle(title);
            video.setDescription(description != null ? description : "");
            video.setOrderIndex(orderIndex);
            video.setVideoType("upload");
            video.setFileName(originalFileName);
            video.setFileSize(file.getSize());
            video.setFilePath(filePath.toString());
            video.setMimeType(file.getContentType());

            Video savedVideo = (dayId != null)
                    ? videoService.createVideoWithDay(video, dayId)
                    : videoService.createVideo(video);

            savedVideo.setUrl(BASE_URL + savedVideo.getFileUrl());
            return ResponseEntity.ok(savedVideo);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // --- GET видео фајл ---
    @GetMapping("/file/{videoId}")
    public ResponseEntity<Resource> getVideoFile(@PathVariable Long videoId) {
        try {
            Video video = videoService.getVideoById(videoId);
            if (video == null || video.getFilePath() == null) return ResponseEntity.notFound().build();

            Path filePath = Paths.get(video.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(
                                video.getMimeType() != null ? video.getMimeType() : "video/mp4"))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=\"" + video.getFileName() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // --- POST YouTube видео ---
    @PostMapping
    public ResponseEntity<Video> createVideo(@RequestBody Video video) {
        try {
            if (video.getVideoType() == null) video.setVideoType("youtube");
            Video createdVideo = videoService.createVideo(video);
            return ResponseEntity.ok(createdVideo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // --- PUT ажурирање видео ---
    @PutMapping("/{id}")
    public ResponseEntity<Video> updateVideo(@PathVariable Long id, @RequestBody Video videoDetails) {
        try {
            Video updatedVideo = videoService.updateVideo(id, videoDetails);
            if (updatedVideo.isUploadedVideo()) updatedVideo.setUrl(BASE_URL + updatedVideo.getFileUrl());
            return ResponseEntity.ok(updatedVideo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // --- DELETE видео ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        try {
            videoService.deleteVideo(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // --- Reorder видеа ---
    @PutMapping("/reorder")
    public ResponseEntity<List<Video>> reorderVideos(@RequestBody List<Long> videoIds) {
        try {
            List<Video> reorderedVideos = videoService.reorderVideos(videoIds);
            reorderedVideos.forEach(v -> {
                if (v.isUploadedVideo()) v.setUrl(BASE_URL + v.getFileUrl());
            });
            return ResponseEntity.ok(reorderedVideos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // --- Update редослед на едно видео ---
    @PutMapping("/{id}/order/{newOrder}")
    public ResponseEntity<Video> updateVideoOrder(@PathVariable Long id, @PathVariable Integer newOrder) {
        try {
            Video updatedVideo = videoService.updateVideoOrder(id, newOrder);
            if (updatedVideo.isUploadedVideo()) updatedVideo.setUrl(BASE_URL + updatedVideo.getFileUrl());
            return ResponseEntity.ok(updatedVideo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
