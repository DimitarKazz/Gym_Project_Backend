package com.example.gym.service;

import com.example.gym.entity.Diet;
import com.example.gym.repository.DietRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DietService {

    @Autowired
    private DietRepository dietRepository;

    public List<Diet> getAllDiets() {
        return dietRepository.findAll();
    }

    public Optional<Diet> getDietById(Long id) {
        return dietRepository.findById(id);
    }

    public Diet createDiet(Diet diet) {
        if (dietRepository.existsByName(diet.getName())) {
            throw new RuntimeException("Diet with this name already exists");
        }
        return dietRepository.save(diet);
    }

    public Diet updateDiet(Long id, Diet dietDetails) {
        Diet diet = dietRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diet not found"));

        diet.setName(dietDetails.getName());
        diet.setDescription(dietDetails.getDescription());
        diet.setPrice(dietDetails.getPrice());

        return dietRepository.save(diet);
    }

    public void deleteDiet(Long id) {
        if (!dietRepository.existsById(id)) {
            throw new RuntimeException("Diet not found");
        }
        dietRepository.deleteById(id);
    }

    public List<Diet> searchDiets(String name) {
        return dietRepository.findByNameContainingIgnoreCase(name);
    }
}