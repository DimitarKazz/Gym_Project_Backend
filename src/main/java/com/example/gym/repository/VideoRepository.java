package com.example.gym.repository;

import com.example.gym.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    List<Video> findByDay_IdOrderByOrderIndexAsc(Long dayId);
    List<Video> findAllByOrderByOrderIndexAsc();
    List<Video> findByMinSubscription_NameOrderByOrderIndexAsc(String subscriptionLevel);
}
