// src/main/java/com/example/gym/repository/UserDietRepository.java
package com.example.gym.repository;

import com.example.gym.entity.UserDiet;
import com.example.gym.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserDietRepository extends JpaRepository<UserDiet, Long> {
    List<UserDiet> findByUserAndIsActiveTrue(User user);
    List<UserDiet> findByUser(User user);
}