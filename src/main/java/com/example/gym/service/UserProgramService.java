package com.example.gym.service;

import com.example.gym.entity.*;
import com.example.gym.repository.UserProgramRepository;
import com.example.gym.repository.UserRepository;
import com.example.gym.repository.ProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserProgramService {

    @Autowired
    private UserProgramRepository userProgramRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Transactional
    public UserProgram purchaseProgram(Long userId, Long programId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new RuntimeException("Program not found"));

        // Check if user already has active program
        Optional<UserProgram> existingProgram = userProgramRepository.findActiveUserProgram(user, program);
        if (existingProgram.isPresent()) {
            throw new RuntimeException("User already has active access to this program");
        }

        // Create new user program
        UserProgram userProgram = new UserProgram(user, program);
        return userProgramRepository.save(userProgram);
    }

    public List<UserProgram> getUserPrograms(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userProgramRepository.findByUser(user);
    }

    public List<UserProgram> getActiveUserPrograms(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userProgramRepository.findValidUserPrograms(user);
    }

    public boolean hasAccessToProgram(Long userId, Long programId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new RuntimeException("Program not found"));

        Optional<UserProgram> userProgram = userProgramRepository.findActiveUserProgram(user, program);
        return userProgram.isPresent() && userProgram.get().isValid();
    }

    public boolean hasAccessToDay(Long userId, Long dayId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserProgram> activePrograms = userProgramRepository.findValidUserPrograms(user);

        return activePrograms.stream()
                .flatMap(up -> up.getProgram().getDays().stream())
                .anyMatch(day -> day.getId().equals(dayId));
    }

    public boolean hasAccessToVideo(Long userId, Long videoId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UserProgram> activePrograms = userProgramRepository.findValidUserPrograms(user);

        return activePrograms.stream()
                .flatMap(up -> up.getProgram().getDays().stream())
                .flatMap(day -> day.getVideos().stream())
                .anyMatch(video -> video.getId().equals(videoId));
    }

    // Scheduled task to decrease remaining days daily
    @Scheduled(cron = "0 0 0 * * ?") // Runs every day at midnight
    @Transactional
    public void decreaseRemainingDaysDaily() {
        List<UserProgram> activePrograms = userProgramRepository.findAll().stream()
                .filter(UserProgram::getIsActive)
                .filter(up -> up.getRemainingDays() > 0)
                .filter(up -> LocalDateTime.now().isBefore(up.getExpiryDate()))
                .toList();

        for (UserProgram userProgram : activePrograms) {
            userProgram.decreaseRemainingDays();
            userProgramRepository.save(userProgram);

            System.out.println("Decreased remaining days for user " + userProgram.getUser().getEmail() +
                    " program " + userProgram.getProgram().getName() +
                    " to " + userProgram.getRemainingDays());
        }
    }

    // Manual method to decrease remaining days (for testing)
    @Transactional
    public void decreaseRemainingDaysForUser(Long userId, Long programId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new RuntimeException("Program not found"));

        Optional<UserProgram> userProgram = userProgramRepository.findByUserAndProgram(user, program);
        if (userProgram.isPresent() && userProgram.get().isValid()) {
            userProgram.get().decreaseRemainingDays();
            userProgramRepository.save(userProgram.get());
        }
    }

    // Get remaining days for a specific program
    public Integer getRemainingDays(Long userId, Long programId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new RuntimeException("Program not found"));

        Optional<UserProgram> userProgram = userProgramRepository.findByUserAndProgram(user, program);
        return userProgram.map(UserProgram::getRemainingDays).orElse(0);
    }

    @Transactional
    public void deactivateExpiredPrograms() {
        List<UserProgram> expiredPrograms = userProgramRepository.findAll().stream()
                .filter(up -> up.getIsActive() &&
                        (up.getRemainingDays() <= 0 || LocalDateTime.now().isAfter(up.getExpiryDate())))
                .toList();

        expiredPrograms.forEach(up -> {
            up.setIsActive(false);
            up.setRemainingDays(0);
        });
        userProgramRepository.saveAll(expiredPrograms);
    }
}