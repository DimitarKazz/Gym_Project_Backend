package com.example.gym.repository;
import com.example.gym.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Locale;

public interface PaymentRepository extends  JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId);


}
