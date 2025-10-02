package com.example.gym.repository;

import com.example.gym.entity.Day;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DayRepository extends JpaRepository<Day, Long> {
    List<Day> findAllByOrderByOrderIndexAsc();
    List<Day> findByTitleContainingIgnoreCase(String title);
}
