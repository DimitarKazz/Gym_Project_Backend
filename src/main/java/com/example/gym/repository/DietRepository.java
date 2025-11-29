package com.example.gym.repository;

import com.example.gym.entity.Diet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DietRepository extends JpaRepository<Diet, Long> {
    List<Diet> findByNameContainingIgnoreCase(String name);
    boolean existsByName(String name);
}