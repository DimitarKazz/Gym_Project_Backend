// src/main/java/com/example/gym/controller/AdminTrainInGymController.java
package com.example.gym.controller;

import com.example.gym.entity.TrainInGym;
import com.example.gym.service.TrainInGymService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/trainings")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminTrainInGymController {

    @Autowired
    private TrainInGymService trainInGymService;

    @GetMapping
    public ResponseEntity<List<TrainInGym>> getAllTrainings() {
        try {
            List<TrainInGym> trainings = trainInGymService.getAllTrainings();
            return ResponseEntity.ok(trainings);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching trainings: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<TrainInGym> createTraining(@RequestBody TrainInGym training) {
        try {
            TrainInGym created = trainInGymService.createTraining(training);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            throw new RuntimeException("Error creating training: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainInGym> getTrainingById(@PathVariable Long id) {
        try {
            TrainInGym training = trainInGymService.getTrainingById(id)
                    .orElseThrow(() -> new RuntimeException("Training not found with id: " + id));
            return ResponseEntity.ok(training);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching training: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainInGym> updateTraining(@PathVariable Long id, @RequestBody TrainInGym training) {
        try {
            TrainInGym updated = trainInGymService.updateTraining(id, training);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            throw new RuntimeException("Error updating training: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTraining(@PathVariable Long id) {
        try {
            trainInGymService.deleteTraining(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting training: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<TrainInGym>> searchTrainings(@RequestParam String name) {
        try {
            List<TrainInGym> trainings = trainInGymService.searchTrainings(name);
            return ResponseEntity.ok(trainings);
        } catch (Exception e) {
            throw new RuntimeException("Error searching trainings: " + e.getMessage());
        }
    }
}