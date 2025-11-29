package com.example.gym.controller;

import com.example.gym.dto.PurchaseDietRequest;
import com.example.gym.entity.Diet;
import com.example.gym.entity.User;
import com.example.gym.entity.UserDiet;
import com.example.gym.repository.UserDietRepository;
import com.example.gym.repository.UserRepository;
import com.example.gym.service.DietService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// ========== PUBLIC DIET CONTROLLER (ЗА USER) ==========
@RestController
@RequestMapping("/api/diets")
class PublicDietController {

    @Autowired
    private DietService dietService;

    @Autowired
    private UserDietRepository userDietRepository;

    @Autowired
    private UserRepository userRepository;

    // PUBLIC endpoint - само име, опис и цена (без authentication)
    @GetMapping
    public ResponseEntity<List<DietDTO>> getAllDiets() {
        System.out.println("🥗 PUBLIC: Request for all diets (no authentication required)");

        List<Diet> diets = dietService.getAllDiets();

        // Мапирај во DTO со само јавни податоци
        List<DietDTO> dietDTOs = diets.stream()
                .map(diet -> new DietDTO(
                        diet.getId(),
                        diet.getName(),
                        diet.getDescription(),
                        diet.getPrice()
                ))
                .collect(Collectors.toList());

        System.out.println("✅ PUBLIC: Returning " + dietDTOs.size() + " diets to frontend");
        return ResponseEntity.ok(dietDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DietDTO> getDietById(@PathVariable Long id) {
        System.out.println("🥗 PUBLIC: Request for diet ID: " + id);

        return dietService.getDietById(id)
                .map(diet -> {
                    DietDTO dto = new DietDTO(
                            diet.getId(),
                            diet.getName(),
                            diet.getDescription(),
                            diet.getPrice()
                    );
                    System.out.println("✅ PUBLIC: Returning diet: " + diet.getName());
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> {
                    System.out.println("❌ PUBLIC: Diet not found with ID: " + id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/search")
    public ResponseEntity<List<DietDTO>> searchDiets(@RequestParam String name) {
        System.out.println("🔍 PUBLIC: Searching diets with name: " + name);

        List<Diet> diets = dietService.searchDiets(name);

        List<DietDTO> dietDTOs = diets.stream()
                .map(diet -> new DietDTO(
                        diet.getId(),
                        diet.getName(),
                        diet.getDescription(),
                        diet.getPrice()
                ))
                .collect(Collectors.toList());

        System.out.println("✅ PUBLIC: Found " + dietDTOs.size() + " diets");
        return ResponseEntity.ok(dietDTOs);
    }

    // Купување на диета со кориснички податоци
    @PostMapping("/{id}/purchase")
    public ResponseEntity<?> purchaseDiet(
            @PathVariable Long id,
            @RequestBody PurchaseDietRequest request) {

        System.out.println("💳 USER: Purchasing diet with ID: " + id);

        try {
            // Земи го корисникот
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !(auth.getPrincipal() instanceof UserDetails)) {
                System.out.println("❌ USER: Not authenticated");
                return ResponseEntity.status(401).body("User not authenticated");
            }

            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String email = userDetails.getUsername();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found: " + email));

            System.out.println("✅ USER: Found user: " + user.getEmail());

            // Земи ја диетата
            Diet diet = dietService.getDietById(id)
                    .orElseThrow(() -> new RuntimeException("Diet not found: " + id));

            System.out.println("✅ USER: Found diet: " + diet.getName());

            // Валидирај ги податоците
            if (request.getHeight() == null || request.getHeight() <= 0) {
                return ResponseEntity.badRequest().body("Висината мора да биде позитивен број");
            }
            if (request.getWeight() == null || request.getWeight() <= 0) {
                return ResponseEntity.badRequest().body("Тежината мора да биде позитивен број");
            }
            if (request.getWaistCircumference() == null || request.getWaistCircumference() <= 0) {
                return ResponseEntity.badRequest().body("Обемот на струк мора да биде позитивен број");
            }

            // Креирај UserDiet запис
            UserDiet userDiet = new UserDiet(
                    user,
                    diet,
                    request.getHeight(),
                    request.getWeight(),
                    request.getWaistCircumference()
            );

            // BMI се пресметува автоматски во конструкторот
            userDietRepository.save(userDiet);

            System.out.println("✅ USER: Diet purchased successfully");
            System.out.println("📊 USER: Height: " + request.getHeight() + " cm");
            System.out.println("📊 USER: Weight: " + request.getWeight() + " kg");
            System.out.println("📊 USER: Waist: " + request.getWaistCircumference() + " cm");
            System.out.println("📊 USER: BMI: " + userDiet.getBmi());
            System.out.println("📊 USER: BMI Category: " + userDiet.getBMICategory());
            System.out.println("📊 USER: Recommended Weight: " + userDiet.getRecommendedWeightRange());

            return ResponseEntity.ok(Map.of(
                    "message", "Диетата е успешно купена!",
                    "dietName", diet.getName(),
                    "bmi", userDiet.getBmi() != null ? userDiet.getBmi() : 0.0,
                    "bmiCategory", userDiet.getBMICategory(),
                    "recommendedWeight", userDiet.getRecommendedWeightRange()
            ));

        } catch (Exception e) {
            System.out.println("❌ USER: Error purchasing diet: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Грешка при купување: " + e.getMessage());
        }
    }

    // Земи ги моите купени диети
    @GetMapping("/my-diets")
    public ResponseEntity<List<UserDiet>> getMyDiets() {
        System.out.println("🥗 USER: Request for my diets");

        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !(auth.getPrincipal() instanceof UserDetails)) {
                System.out.println("❌ USER: Not authenticated");
                return ResponseEntity.status(401).build();
            }

            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String email = userDetails.getUsername();

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<UserDiet> myDiets = userDietRepository.findByUserAndIsActiveTrue(user);

            System.out.println("✅ USER: Found " + myDiets.size() + " diets");

            // Лог за секоја диета со BMI податоци
            for (UserDiet userDiet : myDiets) {
                System.out.println("📊 Diet ID: " + userDiet.getId() +
                        ", BMI: " + userDiet.getBmi() +
                        ", Category: " + userDiet.getBMICategory());
            }

            return ResponseEntity.ok(myDiets);

        } catch (Exception e) {
            System.out.println("❌ USER: Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // DTO Class за Public Data (само основни информации)
    public static class DietDTO {
        private Long id;
        private String name;
        private String description;
        private Object price;

        public DietDTO(Long id, String name, String description, Object price) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Object getPrice() {
            return price;
        }

        public void setPrice(Object price) {
            this.price = price;
        }
    }
}

// ========== ADMIN DIET CONTROLLER (ЗА ADMIN) ==========
@RestController
@RequestMapping("/api/admin/diets")
class AdminDietController {

    @Autowired
    private DietService dietService;

    @GetMapping
    public List<Diet> getAllDiets() {
        System.out.println("📋 ADMIN: Fetching all diets");
        List<Diet> diets = dietService.getAllDiets();
        System.out.println("✅ ADMIN: Returning " + diets.size() + " diets");
        return diets;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Diet> getDietById(@PathVariable Long id) {
        System.out.println("📋 ADMIN: Fetching diet with ID: " + id);
        return dietService.getDietById(id)
                .map(diet -> {
                    System.out.println("✅ ADMIN: Diet found: " + diet.getName());
                    return ResponseEntity.ok(diet);
                })
                .orElseGet(() -> {
                    System.out.println("❌ ADMIN: Diet not found with ID: " + id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public Diet createDiet(@RequestBody Diet diet) {
        System.out.println("📝 ADMIN: Creating new diet: " + diet.getName());
        Diet createdDiet = dietService.createDiet(diet);
        System.out.println("✅ ADMIN: Diet created with ID: " + createdDiet.getId());
        return createdDiet;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Diet> updateDiet(@PathVariable Long id, @RequestBody Diet dietDetails) {
        System.out.println("📝 ADMIN: Updating diet with ID: " + id);
        try {
            Diet updatedDiet = dietService.updateDiet(id, dietDetails);
            System.out.println("✅ ADMIN: Diet updated: " + updatedDiet.getName());
            return ResponseEntity.ok(updatedDiet);
        } catch (RuntimeException e) {
            System.out.println("❌ ADMIN: Failed to update diet: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDiet(@PathVariable Long id) {
        System.out.println("🗑️ ADMIN: Deleting diet with ID: " + id);
        try {
            dietService.deleteDiet(id);
            System.out.println("✅ ADMIN: Diet deleted successfully");
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            System.out.println("❌ ADMIN: Failed to delete diet: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public List<Diet> searchDiets(@RequestParam String name) {
        System.out.println("🔍 ADMIN: Searching diets with name: " + name);
        List<Diet> diets = dietService.searchDiets(name);
        System.out.println("✅ ADMIN: Found " + diets.size() + " diets");
        return diets;
    }
}