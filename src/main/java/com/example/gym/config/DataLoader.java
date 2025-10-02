package com.example.gym.config;

import com.example.gym.entity.Subscription;
import com.example.gym.entity.Video;
import com.example.gym.entity.User;  // ← ДОДАДЕНО
import com.example.gym.entity.Role;  // ← ДОДАДЕНО
import com.example.gym.repository.SubscriptionRepository;
import com.example.gym.repository.VideoRepository;
import com.example.gym.repository.UserRepository;  // ← ДОДАДЕНО
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;  // ← ДОДАДЕНО
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserRepository userRepository;  // ← ДОДАДЕНО

    @Autowired
    private PasswordEncoder passwordEncoder;  // ← ДОДАДЕНО

    @Override
    public void run(String... args) throws Exception {
        // Провери дали веќе има податоци
        if (subscriptionRepository.count() == 0) {
            // Додај ги претплатите
            Subscription basic = new Subscription();
            basic.setName("Basic");
            basic.setPrice(BigDecimal.valueOf(9.99));
            basic.setDurationDays(30);

            Subscription premium = new Subscription();
            premium.setName("Premium");
            premium.setPrice(BigDecimal.valueOf(19.99));
            premium.setDurationDays(60);

            Subscription vip = new Subscription();
            vip.setName("VIP");
            vip.setPrice(BigDecimal.valueOf(29.99));
            vip.setDurationDays(90);

            subscriptionRepository.save(basic);
            subscriptionRepository.save(premium);
            subscriptionRepository.save(vip);

            System.out.println("✅ Претплатите се додадени!");
        }

        if (videoRepository.count() == 0) {
            // Најди ги претплатите за да ги поврзеш со видеата
            Subscription basic = subscriptionRepository.findByName("Basic")
                    .orElseThrow(() -> new RuntimeException("Basic subscription not found"));

            Subscription premium = subscriptionRepository.findByName("Premium")
                    .orElseThrow(() -> new RuntimeException("Premium subscription not found"));

            Subscription vip = subscriptionRepository.findByName("VIP")
                    .orElseThrow(() -> new RuntimeException("VIP subscription not found"));

            // Додај ги видеата
            Video video1 = new Video();
            video1.setTitle("Основен тренинг за почетници");
            video1.setDescription("Основни вежби за почетници со детални објаснувања");
            video1.setUrl("https://youtube.com/embed/abc123");
            video1.setOrderIndex(1);
            video1.setMinSubscription(basic);

            Video video2 = new Video();
            video2.setTitle("Напреден тренинг за сила");
            video2.setDescription("Вежби за зголемување на силата и мускулната маса");
            video2.setUrl("https://youtube.com/embed/def456");
            video2.setOrderIndex(2);
            video2.setMinSubscription(premium);

            Video video3 = new Video();
            video3.setTitle("Професионален тренинг");
            video3.setDescription("Напредни техники за професионални атлетичари");
            video3.setUrl("https://youtube.com/embed/ghi789");
            video3.setOrderIndex(3);
            video3.setMinSubscription(vip);

            videoRepository.save(video1);
            videoRepository.save(video2);
            videoRepository.save(video3);

            System.out.println("✅ Видеата се додадени!");
        }

        // Додај го ADMIN корисникот (надвор од video проверката)
        if (userRepository.count() == 0) {
            User adminUser = new User();
            adminUser.setEmail("admin@gym.com");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setRole(Role.ADMIN);
            userRepository.save(adminUser);

            System.out.println("✅ Admin корисник креиран: admin@gym.com / admin123");
        }

        System.out.println("✅ Сите тестни податоци се подготвени!");
    }
}