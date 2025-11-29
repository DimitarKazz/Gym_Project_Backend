package com.example.gym.service;

import com.example.gym.entity.*;
import com.example.gym.repository.UserTrainInGymRepository;
import com.example.gym.repository.UserRepository;
import com.example.gym.repository.TrainInGymRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserTrainInGymService {

    @Autowired
    private UserTrainInGymRepository userTrainInGymRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrainInGymRepository trainInGymRepository;

    @Transactional
    public UserTrainInGym purchaseTraining(Long userId, Long trainingId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TrainInGym training = trainInGymRepository.findById(trainingId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        // Check if user already has active training
        Optional<UserTrainInGym> existingTraining = userTrainInGymRepository.findActiveUserTraining(user, training);
        if (existingTraining.isPresent()) {
            throw new RuntimeException("User already has active access to this training");
        }

        // Create new user training
        UserTrainInGym userTraining = new UserTrainInGym(user, training);
        return userTrainInGymRepository.save(userTraining);
    }

    public List<UserTrainInGym> getUserTrainings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userTrainInGymRepository.findByUser(user);
    }

    public List<UserTrainInGym> getActiveUserTrainings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userTrainInGymRepository.findValidUserTrainings(user);
    }

    public boolean hasAccessToTraining(Long userId, Long trainingId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TrainInGym training = trainInGymRepository.findById(trainingId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        Optional<UserTrainInGym> userTraining = userTrainInGymRepository.findActiveUserTraining(user, training);
        return userTraining.isPresent() && userTraining.get().isValid();
    }

    // Scheduled task to decrease remaining days daily
    @Scheduled(cron = "0 0 0 * * ?") // Runs every day at midnight
    @Transactional
    public void decreaseRemainingDaysDaily() {
        List<UserTrainInGym> activeTrainings = userTrainInGymRepository.findAllActiveTrainings();

        for (UserTrainInGym userTraining : activeTrainings) {
            userTraining.decreaseRemainingDays();
            userTrainInGymRepository.save(userTraining);

            System.out.println("Decreased remaining days for user " + userTraining.getUser().getEmail() +
                    " training " + userTraining.getTrainInGym().getName() +
                    " to " + userTraining.getRemainingDays());

            // Auto-delete if remaining days reach 0
            if (userTraining.getRemainingDays() <= 0) {
                userTrainInGymRepository.delete(userTraining);
                System.out.println("Auto-deleted expired training for user: " + userTraining.getUser().getEmail());
            }
        }
    }

    // Manual method to decrease remaining days (for testing)
    @Transactional
    public void decreaseRemainingDaysForUser(Long userId, Long trainingId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TrainInGym training = trainInGymRepository.findById(trainingId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        Optional<UserTrainInGym> userTraining = userTrainInGymRepository.findByUserAndTrainInGym(user, training);
        if (userTraining.isPresent() && userTraining.get().isValid()) {
            userTraining.get().decreaseRemainingDays();
            userTrainInGymRepository.save(userTraining.get());

            // Auto-delete if remaining days reach 0
            if (userTraining.get().getRemainingDays() <= 0) {
                userTrainInGymRepository.delete(userTraining.get());
            }
        }
    }

    // Get remaining days for a specific training
    public Integer getRemainingDays(Long userId, Long trainingId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TrainInGym training = trainInGymRepository.findById(trainingId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        Optional<UserTrainInGym> userTraining = userTrainInGymRepository.findByUserAndTrainInGym(user, training);
        return userTraining.map(UserTrainInGym::getRemainingDays).orElse(0);
    }

    @Transactional
    public void deactivateExpiredTrainings() {
        List<UserTrainInGym> expiredTrainings = userTrainInGymRepository.findAll().stream()
                .filter(utg -> utg.getIsActive() &&
                        (utg.getRemainingDays() <= 0 || LocalDateTime.now().isAfter(utg.getExpiryDate())))
                .toList();

        // Delete expired trainings instead of deactivating
        userTrainInGymRepository.deleteAll(expiredTrainings);
    }

    // Delete training (when remaining days reach 0)
    @Transactional
    public void deleteUserTraining(Long userTrainingId) {
        userTrainInGymRepository.deleteById(userTrainingId);
    }
}