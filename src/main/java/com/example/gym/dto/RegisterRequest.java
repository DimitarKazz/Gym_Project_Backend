// src/main/java/com/example/gym/dto/RegisterRequest.java
package com.example.gym.dto;

public class RegisterRequest {
    private String email;
    private String password;
    private String firstName;  // 👈 НОВО
    private String lastName;   // 👈 НОВО
    private String gender;     // 👈 НОВО
    private String role;       // Optional

    // Constructors
    public RegisterRequest() {}

    public RegisterRequest(String email, String password, String firstName, String lastName, String gender) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    public RegisterRequest(String email, String password, String firstName, String lastName, String gender, String role) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.role = role;
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}