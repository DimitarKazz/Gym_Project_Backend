package com.example.gym.controller;

import com.example.gym.entity.Day;
import com.example.gym.entity.Video;
import com.example.gym.entity.User;
import com.example.gym.service.DayService;
import com.example.gym.service.VideoService;
import com.example.gym.service.UserProgramService;
import com.example.gym.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/days")
public class DayController {

    @Autowired
    private DayService dayService;

    @Autowired
    private VideoService videoService;

    @Autowired
    private UserProgramService userProgramService;

    @Autowired
    private UserRepository userRepository;

    // Сите денови - ВРАЌА ПРАЗНА ЛИСТА доколку нема пристап
    @GetMapping
    public ResponseEntity<List<Day>> getAllDays(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        try {
            // Провери дали корисникот е логиран
            if (userDetails == null) {
                return ResponseEntity.ok(List.of()); // Врати празна листа за нелогирани корисници
            }

            Long userId = getUserIdFromAuthentication(userDetails);

            // Земи ги сите денови
            List<Day> allDays = dayService.getAllDays();

            // Филтрирај ги само деновите до кои корисникот има пристап
            List<Day> accessibleDays = allDays.stream()
                    .filter(day -> userProgramService.hasAccessToDay(userId, day.getId()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(accessibleDays);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Конкретен ден - ВРАТИ 403 ДОКолку нема пристап
    @GetMapping("/{id}")
    public ResponseEntity<Day> getDayById(@PathVariable Long id, @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        try {
            // Провери дали корисникот е логиран
            if (userDetails == null) {
                return ResponseEntity.status(401).build(); // Unauthorized
            }

            Long userId = getUserIdFromAuthentication(userDetails);

            // Провери дали корисникот има пристап до овој ден
            if (!userProgramService.hasAccessToDay(userId, id)) {
                return ResponseEntity.status(403).build(); // Forbidden - нема пристап
            }

            Day day = dayService.getDayById(id);
            return ResponseEntity.ok(day);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Видеа за конкретен ден - ВРАТИ 403 ДОКолку нема пристап
    @GetMapping("/{id}/videos")
    public ResponseEntity<List<Video>> getVideosForDay(@PathVariable Long id, @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        try {
            // Провери дали корисникот е логиран
            if (userDetails == null) {
                return ResponseEntity.status(401).build(); // Unauthorized
            }

            Long userId = getUserIdFromAuthentication(userDetails);

            // Провери дали корисникот има пристап до овој ден
            if (!userProgramService.hasAccessToDay(userId, id)) {
                return ResponseEntity.status(403).build(); // Forbidden - нема пристап
            }

            List<Video> videos = videoService.getVideosByDay(id);
            return ResponseEntity.ok(videos);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Административни endpoints (само за администратори)
    @PostMapping
    public ResponseEntity<Day> createDay(@RequestBody Day day, @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        try {
            checkAdminAccess(userDetails);
            Day createdDay = dayService.createDay(day);
            return ResponseEntity.ok(createdDay);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Day> updateDay(@PathVariable Long id, @RequestBody Day dayDetails, @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        try {
            checkAdminAccess(userDetails);
            Day updatedDay = dayService.updateDay(id, dayDetails);
            return ResponseEntity.ok(updatedDay);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDay(@PathVariable Long id, @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        try {
            checkAdminAccess(userDetails);
            dayService.deleteDay(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/reorder")
    public ResponseEntity<List<Day>> reorderDays(@RequestBody List<Long> dayIds, @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        try {
            checkAdminAccess(userDetails);
            List<Day> reorderedDays = dayService.reorderDays(dayIds);
            return ResponseEntity.ok(reorderedDays);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Helper methods
    private Long getUserIdFromAuthentication(org.springframework.security.core.userdetails.User userDetails) {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    private void checkAdminAccess(org.springframework.security.core.userdetails.User userDetails) {
        if (userDetails == null) {
            throw new RuntimeException("Authentication required");
        }

        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("Admin access required");
        }
    }
}