// src/main/java/com/example/gym/dto/AuthResponse.java
package com.example.gym.dto;

import com.example.gym.entity.User;

public class AuthResponse {
    private String token;
    private String message;
    private String email;
    private String firstName;
    private String lastName;
    private String gender;
    private String role;

    // Default constructor
    public AuthResponse() {}

    // Конструктор со токен и корисник (за успешен login)
    public AuthResponse(String token, User user) {
        this.token = token;
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.gender = user.getGender();
        this.role = user.getRole().toString();
        this.message = "Login successful";
    }

    // Конструктор со токен, корисник и custom порака
    public AuthResponse(String token, User user, String message) {
        this.token = token;
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.gender = user.getGender();
        this.role = user.getRole().toString();
        this.message = message;
    }

    // 👇 ИЗБРИШАН: Конфликтен конструктор
    // public AuthResponse(String token) { ... } // REMOVED

    // 👇 Static factory method за error response (замена за конструкторот со само message)
    public static AuthResponse error(String message) {
        AuthResponse response = new AuthResponse();
        response.setMessage(message);
        return response;
    }

    // 👇 Static factory method за token-only response (замена за конструкторот со само token)
    public static AuthResponse tokenOnly(String token) {
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        return response;
    }

    // Getters & Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}