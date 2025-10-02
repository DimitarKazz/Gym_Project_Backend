package com.example.gym.controller;

import com.example.gym.entity.Day;
import com.example.gym.service.DayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/days")
public class AdminDayController {

    @Autowired
    private DayService dayService;

    @GetMapping
    public ResponseEntity<List<Day>> getAllDays() {
        return ResponseEntity.ok(dayService.getAllDays());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Day> getDayById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(dayService.getDayById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Day> createDay(@RequestBody Day day) {
        try {
            return ResponseEntity.ok(dayService.createDay(day));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Day> updateDay(@PathVariable Long id, @RequestBody Day dayDetails) {
        try {
            return ResponseEntity.ok(dayService.updateDay(id, dayDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDay(@PathVariable Long id) {
        try {
            dayService.deleteDay(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Реorder денови
    @PutMapping("/reorder")
    public ResponseEntity<List<Day>> reorderDays(@RequestBody List<Long> dayIds) {
        try {
            return ResponseEntity.ok(dayService.reorderDays(dayIds));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
