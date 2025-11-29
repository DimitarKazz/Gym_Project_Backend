// src/main/java/com/example/gym/service/TrainInGymService.java
package com.example.gym.service;

import com.example.gym.entity.TrainInGym;
import com.example.gym.repository.TrainInGymRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TrainInGymService {

    @Autowired
    private TrainInGymRepository trainInGymRepository;

    public List<TrainInGym> getAllTrainings() {
        try {
            return trainInGymRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all trainings: " + e.getMessage());
        }
    }

    public Optional<TrainInGym> getTrainingById(Long id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("Training ID must be a positive number");
            }
            return trainInGymRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching training by ID: " + e.getMessage());
        }
    }

    public TrainInGym createTraining(TrainInGym training) {
        try {
            // Валидација на податоци
            validateTraining(training);

            // Проверка дали постои тренинг со исто име
            if (trainInGymRepository.existsByName(training.getName())) {
                throw new RuntimeException("Training with name '" + training.getName() + "' already exists");
            }

            // Зачувај го тренингот
            return trainInGymRepository.save(training);
        } catch (Exception e) {
            throw new RuntimeException("Error creating training: " + e.getMessage());
        }
    }

    public TrainInGym updateTraining(Long id, TrainInGym trainingDetails) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("Training ID must be a positive number");
            }

            // Валидација на новите податоци
            validateTraining(trainingDetails);

            // Најди го постоечкиот тренинг
            TrainInGym training = trainInGymRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Training not found with id: " + id));

            // Проверка дали некој друг тренинг има исто име
            if (!training.getName().equals(trainingDetails.getName()) &&
                    trainInGymRepository.existsByName(trainingDetails.getName())) {
                throw new RuntimeException("Training with name '" + trainingDetails.getName() + "' already exists");
            }

            // Ажурирај ги податоците
            training.setName(trainingDetails.getName());
            training.setDescription(trainingDetails.getDescription());
            training.setPrice(trainingDetails.getPrice());
            training.setDurationDays(trainingDetails.getDurationDays());

            return trainInGymRepository.save(training);
        } catch (Exception e) {
            throw new RuntimeException("Error updating training: " + e.getMessage());
        }
    }

    public void deleteTraining(Long id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("Training ID must be a positive number");
            }

            if (!trainInGymRepository.existsById(id)) {
                throw new RuntimeException("Training not found with id: " + id);
            }

            trainInGymRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting training: " + e.getMessage());
        }
    }

    public List<TrainInGym> searchTrainings(String name) {
        try {
            if (name == null || name.trim().isEmpty()) {
                return getAllTrainings();
            }
            return trainInGymRepository.findByNameContainingIgnoreCase(name.trim());
        } catch (Exception e) {
            throw new RuntimeException("Error searching trainings: " + e.getMessage());
        }
    }

    // Дополнителни методи
    public List<TrainInGym> getTrainingsByDuration(Integer durationDays) {
        try {
            return trainInGymRepository.findByDurationDays(durationDays);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching trainings by duration: " + e.getMessage());
        }
    }

    public List<TrainInGym> getTrainingsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        try {
            return trainInGymRepository.findByPriceRange(minPrice, maxPrice);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching trainings by price range: " + e.getMessage());
        }
    }

    // Приватен метод за валидација
    private void validateTraining(TrainInGym training) {
        if (training == null) {
            throw new IllegalArgumentException("Training cannot be null");
        }

        if (training.getName() == null || training.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Training name is required");
        }

        if (training.getName().length() > 255) {
            throw new IllegalArgumentException("Training name cannot exceed 255 characters");
        }

        if (training.getDescription() != null && training.getDescription().length() > 1000) {
            throw new IllegalArgumentException("Training description cannot exceed 1000 characters");
        }

        if (training.getPrice() == null || training.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Training price must be greater than 0");
        }

        if (training.getDurationDays() == null || training.getDurationDays() <= 0) {
            throw new IllegalArgumentException("Training duration days must be greater than 0");
        }

        if (training.getDurationDays() > 365) {
            throw new IllegalArgumentException("Training duration days cannot exceed 365 days");
        }
    }
}