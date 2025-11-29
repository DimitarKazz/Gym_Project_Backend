package com.example.gym.controller;

import com.example.gym.entity.TrainInGym;
import com.example.gym.entity.UserTrainInGym;
import com.example.gym.service.TrainInGymService;
import com.example.gym.service.UserTrainInGymService;
import com.example.gym.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/trainings")
@CrossOrigin(origins = "http://localhost:3000")
public class TrainInGymController {

    @Autowired
    private TrainInGymService trainInGymService;

    @Autowired
    private UserTrainInGymService userTrainInGymService;

    @Autowired
    private UserRepository userRepository;

    // ==== ЈАВНИ ENDPOINTS - БЕЗ АВТЕНТИКАЦИЈА ====

    /**
     * Јавни информации за сите тренинзи (достапно за сите корисници)
     * Се користи за прикажување на основни информации на јавната страница
     */
    @GetMapping
    public ResponseEntity<List<TrainInGym>> getAllTrainings() {
        try {
            System.out.println("🏋️ PUBLIC: Request for all trainings (no authentication required)");
            List<TrainInGym> trainings = trainInGymService.getAllTrainings();

            // Логирај детали за секој тренинг
            trainings.forEach(training -> {
                System.out.println("✅ PUBLIC: Training - ID: " + training.getId() +
                        ", Name: " + training.getName() +
                        ", Price: " + training.getPrice() +
                        ", Duration: " + training.getDurationDays() + " days");
            });

            System.out.println("✅ PUBLIC: Returning " + trainings.size() + " trainings to frontend");
            return ResponseEntity.ok(trainings);
        } catch (Exception e) {
            System.err.println("❌ PUBLIC: Error in getAllTrainings: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Јавни информации за еден тренинг (достапно за сите корисници)
     */
    @GetMapping("/{id}")
    public ResponseEntity<TrainInGym> getTrainingById(@PathVariable Long id) {
        try {
            System.out.println("🏋️ PUBLIC: Request for training with ID: " + id + " (no authentication required)");

            TrainInGym training = trainInGymService.getTrainingById(id)
                    .orElseThrow(() -> new RuntimeException("Training not found with id: " + id));

            System.out.println("✅ PUBLIC: Returning training - ID: " + training.getId() +
                    ", Name: " + training.getName() +
                    ", Price: " + training.getPrice());
            return ResponseEntity.ok(training);
        } catch (RuntimeException e) {
            System.err.println("❌ PUBLIC: Training not found with ID: " + id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("❌ PUBLIC: Unexpected error getting training by ID: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==== АВТЕНТИКУВАНИ ENDPOINTS - БАРААТ НАЈАВА ====

    /**
     * Купување на тренинг (бара автентикација)
     */
    @PostMapping("/{id}/purchase")
    public ResponseEntity<?> purchaseTraining(@PathVariable Long id,
                                              @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        try {
            Long userId = getUserIdFromAuthentication(userDetails);
            System.out.println("💳 AUTH: User ID " + userId + " attempting to purchase training: " + id);

            UserTrainInGym userTraining = userTrainInGymService.purchaseTraining(userId, id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Training purchased successfully");
            response.put("userTraining", userTraining);
            response.put("remainingDays", userTraining.getRemainingDays());

            System.out.println("✅ AUTH: Training " + id + " successfully purchased by user " + userId +
                    " with " + userTraining.getRemainingDays() + " days");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println("❌ AUTH: Error purchasing training: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Purchase failed",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            System.err.println("❌ AUTH: Unexpected error purchasing training: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Мои купени тренинзи (бара автентикација)
     */
    @GetMapping("/my-trainings")
    public ResponseEntity<List<UserTrainInGym>> getMyTrainings(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        try {
            Long userId = getUserIdFromAuthentication(userDetails);
            System.out.println("🏋️ AUTH: User ID " + userId + " requesting their purchased trainings");

            List<UserTrainInGym> userTrainings = userTrainInGymService.getActiveUserTrainings(userId);

            System.out.println("✅ AUTH: Returning " + userTrainings.size() + " active trainings for user " + userId);
            return ResponseEntity.ok(userTrainings);
        } catch (RuntimeException e) {
            System.err.println("❌ AUTH: Error getting user trainings: " + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("❌ AUTH: Unexpected error getting user trainings: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Преостанати денови за тренинг (бара автентикација)
     */
    @GetMapping("/{id}/remaining-days")
    public ResponseEntity<?> getRemainingDays(@PathVariable Long id,
                                              @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        try {
            Long userId = getUserIdFromAuthentication(userDetails);
            System.out.println("📅 AUTH: User ID " + userId + " checking remaining days for training: " + id);

            Integer remainingDays = userTrainInGymService.getRemainingDays(userId, id);

            Map<String, Object> response = new HashMap<>();
            response.put("trainingId", id);
            response.put("remainingDays", remainingDays);
            response.put("hasAccess", remainingDays > 0);

            System.out.println("✅ AUTH: User " + userId + " has " + remainingDays + " remaining days for training " + id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println("❌ AUTH: Error getting remaining days: " + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("❌ AUTH: Unexpected error getting remaining days: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Намалување на денови (за тестирање или админи)
     */
    @PostMapping("/{id}/decrease-days")
    public ResponseEntity<?> decreaseRemainingDays(@PathVariable Long id,
                                                   @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        try {
            Long userId = getUserIdFromAuthentication(userDetails);
            System.out.println("📉 AUTH: User ID " + userId + " decreasing days for training: " + id);

            userTrainInGymService.decreaseRemainingDaysForUser(userId, id);
            Integer remainingDays = userTrainInGymService.getRemainingDays(userId, id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Remaining days decreased successfully");
            response.put("remainingDays", remainingDays);

            System.out.println("✅ AUTH: Days decreased for user " + userId +
                    ", remaining: " + remainingDays + " days for training " + id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println("❌ AUTH: Error decreasing days: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Failed to decrease days",
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            System.err.println("❌ AUTH: Unexpected error decreasing days: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==== HELPER МЕТОДИ ====

    /**
     * Извлекува го User ID од authentication објектот
     */
    private Long getUserIdFromAuthentication(org.springframework.security.core.userdetails.User userDetails) {
        try {
            String email = userDetails.getUsername();
            System.out.println("🔍 AUTH: Looking up user by email: " + email);

            com.example.gym.entity.User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

            System.out.println("✅ AUTH: Found user - ID: " + user.getId() + ", Email: " + email);
            return user.getId();
        } catch (Exception e) {
            System.err.println("❌ AUTH: Error getting user ID from authentication: " + e.getMessage());
            throw new RuntimeException("Failed to get user from authentication", e);
        }
    }
}