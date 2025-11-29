// src/main/java/com/example/gym/repository/TrainInGymRepository.java
package com.example.gym.repository;

import com.example.gym.entity.TrainInGym;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainInGymRepository extends JpaRepository<TrainInGym, Long> {

    // Проверка дали постои тренинг со исто име
    boolean existsByName(String name);

    // Пребарување тренинзи по име (case insensitive)
    List<TrainInGym> findByNameContainingIgnoreCase(String name);

    // Најди ги сите активни тренинзи (ако имаш active поле)
    // List<TrainInGym> findByActiveTrue();

    // Најди тренинзи во одреден ценовен опсег
    @Query("SELECT t FROM TrainInGym t WHERE t.price BETWEEN :minPrice AND :maxPrice")
    List<TrainInGym> findByPriceRange(@Param("minPrice") java.math.BigDecimal minPrice,
                                      @Param("maxPrice") java.math.BigDecimal maxPrice);

    // Најди тренинзи по времетраење
    List<TrainInGym> findByDurationDays(Integer durationDays);

    // Најди тренинзи со времетраење помало од одредена вредност
    List<TrainInGym> findByDurationDaysLessThanEqual(Integer maxDuration);

    // Најди тренинзи со времетраење поголемо од одредена вредност
    List<TrainInGym> findByDurationDaysGreaterThanEqual(Integer minDuration);
}