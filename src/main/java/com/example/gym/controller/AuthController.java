// src/main/java/com/example/gym/controller/AuthController.java
package com.example.gym.controller;

import com.example.gym.dto.RegisterRequest;
import com.example.gym.entity.Role;
import com.example.gym.entity.User;
import com.example.gym.repository.UserRepository;
import com.example.gym.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 👇 LOGIN - само email и password
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            System.out.println("🔐 Login attempt for: " + authRequest.getEmail());

            // Провери дали корисникот постои
            User user = userRepository.findByEmail(authRequest.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + authRequest.getEmail()));

            // Земи UserDetails
            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());

            // Провери password
            if (!passwordEncoder.matches(authRequest.getPassword(), userDetails.getPassword())) {
                System.out.println("❌ Invalid password for: " + authRequest.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Invalid credentials"));
            }

            // Ажурирај lastLogin
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            // Генерирај JWT токен
            String token = jwtUtil.generateToken(user.getEmail());

            // Креирај response
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("email", user.getEmail());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("gender", user.getGender());
            response.put("role", user.getRole().toString());
            response.put("message", "Login successful");

            System.out.println("✅ Login successful for: " + user.getEmail() + " (" + user.getFullName() + ")");
            return ResponseEntity.ok(response);

        } catch (UsernameNotFoundException e) {
            System.out.println("❌ User not found: " + authRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials"));
        } catch (Exception e) {
            System.err.println("❌ Login error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Server error"));
        }
    }

    // 👇 REGISTER - email, password, firstName, lastName, gender
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            System.out.println("📝 Registration attempt for: " + registerRequest.getEmail());

            // Валидација
            if (registerRequest.getEmail() == null || registerRequest.getEmail().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
            }

            if (registerRequest.getPassword() == null || registerRequest.getPassword().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Password is required"));
            }

            if (registerRequest.getFirstName() == null || registerRequest.getFirstName().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "First name is required"));
            }

            if (registerRequest.getLastName() == null || registerRequest.getLastName().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Last name is required"));
            }

            if (registerRequest.getGender() == null || registerRequest.getGender().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Gender is required"));
            }

            // Провери дали корисникот веќе постои
            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                System.out.println("❌ Email already exists: " + registerRequest.getEmail());
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Email already exists"));
            }

            // Креирај нов корисник
            User user = new User();
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setFirstName(registerRequest.getFirstName());
            user.setLastName(registerRequest.getLastName());
            user.setGender(registerRequest.getGender());

            // Постави role - ако е испратена, користи ја, инаку USER
            if (registerRequest.getRole() != null && !registerRequest.getRole().isEmpty()) {
                try {
                    user.setRole(Role.valueOf(registerRequest.getRole().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    user.setRole(Role.USER);
                }
            } else {
                user.setRole(Role.USER);
            }

            // Зачувај го корисникот
            User savedUser = userRepository.save(user);

            // Креирај response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration successful");
            response.put("email", savedUser.getEmail());
            response.put("firstName", savedUser.getFirstName());
            response.put("lastName", savedUser.getLastName());
            response.put("gender", savedUser.getGender());
            response.put("role", savedUser.getRole().toString());

            System.out.println("✅ Registration successful: " + savedUser.getEmail() +
                    " (" + savedUser.getFullName() + ", " + savedUser.getGender() + ")");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Registration error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Server error: " + e.getMessage()));
        }
    }

    // DTO за login - само email и password
    public static class AuthRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}