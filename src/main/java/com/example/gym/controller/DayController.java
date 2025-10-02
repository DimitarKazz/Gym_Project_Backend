package com.example.gym.controller;

import com.example.gym.entity.Day;
import com.example.gym.entity.Video; // ← ДОДАДЕНО
import com.example.gym.service.DayService;
import com.example.gym.service.VideoService; // ← ДОДАДЕНО
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/days")
public class DayController {

    @Autowired
    private DayService dayService;

    @Autowired // ← ДОДАДЕНО
    private VideoService videoService;

    @GetMapping
    public ResponseEntity<List<Day>> getAllDays() {
        List<Day> days = dayService.getAllDays();
        return ResponseEntity.ok(days);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Day> getDayById(@PathVariable Long id) {
        Day day = dayService.getDayById(id);
        return ResponseEntity.ok(day);
    }

    @PostMapping
    public ResponseEntity<Day> createDay(@RequestBody Day day) {
        Day createdDay = dayService.createDay(day);
        return ResponseEntity.ok(createdDay);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Day> updateDay(@PathVariable Long id, @RequestBody Day dayDetails) {
        Day updatedDay = dayService.updateDay(id, dayDetails);
        return ResponseEntity.ok(updatedDay);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDay(@PathVariable Long id) {
        dayService.deleteDay(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reorder")
    public ResponseEntity<List<Day>> reorderDays(@RequestBody List<Long> dayIds) {
        List<Day> reorderedDays = dayService.reorderDays(dayIds);
        return ResponseEntity.ok(reorderedDays);
    }

    @GetMapping("/{id}/videos")
    public ResponseEntity<List<Video>> getVideosForDay(@PathVariable Long id) {
        try {
            List<Video> videos = videoService.getVideosByDay(id);
            return ResponseEntity.ok(videos);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}