// src/main/java/com/example/gym/config/DataLoader.java - СО ИМЕ, ПРЕЗИМЕ И ПОЛ
package com.example.gym.config;

import com.example.gym.entity.Subscription;
import com.example.gym.entity.Video;
import com.example.gym.entity.User;
import com.example.gym.entity.Role;
import com.example.gym.entity.Program;
import com.example.gym.entity.TrainInGym;
import com.example.gym.entity.Day;
import com.example.gym.entity.Diet;
import com.example.gym.repository.SubscriptionRepository;
import com.example.gym.repository.VideoRepository;
import com.example.gym.repository.UserRepository;
import com.example.gym.repository.ProgramRepository;
import com.example.gym.repository.TrainInGymRepository;
import com.example.gym.repository.DayRepository;
import com.example.gym.repository.DietRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private TrainInGymRepository trainInGymRepository;

    @Autowired
    private DayRepository dayRepository;

    @Autowired
    private DietRepository dietRepository;

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

        // 👇 АЖУРИРАНО: Додај ги корисниците СО ИМЕ, ПРЕЗИМЕ И ПОЛ
        if (userRepository.count() == 0) {
            // Admin корисник
            User adminUser = new User();
            adminUser.setEmail("admin@gym.com");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setFirstName("Админ");           // 👈 НОВО
            adminUser.setLastName("Администратор");    // 👈 НОВО
            adminUser.setGender("Male");                // 👈 НОВО
            adminUser.setRole(Role.ADMIN);
            userRepository.save(adminUser);

            // Тест корисник (USER)
            User testUser = new User();
            testUser.setEmail("user@test.com");
            testUser.setPassword(passwordEncoder.encode("test123"));
            testUser.setFirstName("Тест");              // 👈 НОВО
            testUser.setLastName("Корисник");           // 👈 НОВО
            testUser.setGender("Male");                 // 👈 НОВО
            testUser.setRole(Role.USER);
            userRepository.save(testUser);

            // 👇 НОВО: Додај уште неколку тест корисници
            User dimitarUser = new User();
            dimitarUser.setEmail("dimitar@gym.com");
            dimitarUser.setPassword(passwordEncoder.encode("dimitar123"));
            dimitarUser.setFirstName("Димитар");
            dimitarUser.setLastName("Казазовски");
            dimitarUser.setGender("Male");
            dimitarUser.setRole(Role.USER);
            userRepository.save(dimitarUser);

            User marijaUser = new User();
            marijaUser.setEmail("marija@gym.com");
            marijaUser.setPassword(passwordEncoder.encode("marija123"));
            marijaUser.setFirstName("Марија");
            marijaUser.setLastName("Петровска");
            marijaUser.setGender("Female");
            marijaUser.setRole(Role.USER);
            userRepository.save(marijaUser);

            User stefanUser = new User();
            stefanUser.setEmail("stefan@gym.com");
            stefanUser.setPassword(passwordEncoder.encode("stefan123"));
            stefanUser.setFirstName("Стефан");
            stefanUser.setLastName("Николовски");
            stefanUser.setGender("Male");
            stefanUser.setRole(Role.USER);
            userRepository.save(stefanUser);

            System.out.println("✅ Admin корисник креиран: admin@gym.com / admin123 (Админ Администратор)");
            System.out.println("✅ Тест корисник креиран: user@test.com / test123 (Тест Корисник)");
            System.out.println("✅ Димитар корисник креиран: dimitar@gym.com / dimitar123 (Димитар Казазовски)");
            System.out.println("✅ Марија корисник креиран: marija@gym.com / marija123 (Марија Петровска)");
            System.out.println("✅ Стефан корисник креиран: stefan@gym.com / stefan123 (Стефан Николовски)");
        }

        // Додај ги програмите
        if (programRepository.count() == 0) {
            Program program1 = new Program("Почетен програм", "Програм за почетници", BigDecimal.valueOf(99.99), 30);
            Program program2 = new Program("Напреден програм", "Програм за напредни", BigDecimal.valueOf(199.99), 60);
            Program program3 = new Program("Професионален програм", "Програм за професионалци", BigDecimal.valueOf(299.99), 90);

            programRepository.save(program1);
            programRepository.save(program2);
            programRepository.save(program3);

            System.out.println("✅ Програмите се додадени!");
        }

        // Додај диети
        if (dietRepository.count() == 0) {
            Diet diet1 = new Diet();
            diet1.setName("Кетогена диета");
            diet1.setDescription("Ниско јаглени хидрати, високи масти. Подобрено чувство за енергија и намалување на телесната маса.");
            diet1.setPrice(BigDecimal.valueOf(2500)); // цена во денари
            dietRepository.save(diet1);

            Diet diet2 = new Diet();
            diet2.setName("Веган диета");
            diet2.setDescription("Растителна исхрана без животински производи, богата со влакна и витамини.");
            diet2.setPrice(BigDecimal.valueOf(2000));
            dietRepository.save(diet2);

            Diet diet3 = new Diet();
            diet3.setName("Балансирана диета");
            diet3.setDescription("Стабилен однос на протеини, јаглехидрати и масти за одржување на форма.");
            diet3.setPrice(BigDecimal.valueOf(1800));
            dietRepository.save(diet3);

            System.out.println("✅ Диетите се додадени!");
        }

        // Додај ги тренинзите
        if (trainInGymRepository.count() == 0) {
            TrainInGym training1 = new TrainInGym("Персонален тренинг", "Индивидуален тренинг со личен тренер", BigDecimal.valueOf(29.99), 30);
            TrainInGym training2 = new TrainInGym("Групен тренинг", "Тренинг во мала група", BigDecimal.valueOf(19.99), 15);
            TrainInGym training3 = new TrainInGym("Интензивен тренинг", "Напреден тренинг за искусни", BigDecimal.valueOf(39.99), 60);

            trainInGymRepository.save(training1);
            trainInGymRepository.save(training2);
            trainInGymRepository.save(training3);

            System.out.println("✅ Тренинзите се додадени!");
        }

        // Додај ги деновите
        if (dayRepository.count() == 0) {
            Day day1 = new Day();
            day1.setTitle("Ден 1: Вовед");
            day1.setDescription("Вовед во програмата и основни вежби");
            day1.setOrderIndex(1);
            dayRepository.save(day1);

            Day day2 = new Day();
            day2.setTitle("Ден 2: Горен дел на телото");
            day2.setDescription("Вежби за граден мишич, раменици и раце");
            day2.setOrderIndex(2);
            dayRepository.save(day2);

            Day day3 = new Day();
            day3.setTitle("Ден 3: Долен дел на телото");
            day3.setDescription("Вежби за нозе и стомак");
            day3.setOrderIndex(3);
            dayRepository.save(day3);

            System.out.println("✅ Деновите се додадени!");
        }

        System.out.println("✅ Сите тестни податоци се подготвени!");
        System.out.println("=====================================");
        System.out.println("📋 ТЕСТ КОРИСНИЦИ:");
        System.out.println("👤 Admin: admin@gym.com / admin123");
        System.out.println("👤 User: user@test.com / test123");
        System.out.println("👤 Димитар: dimitar@gym.com / dimitar123");
        System.out.println("👤 Марија: marija@gym.com / marija123");
        System.out.println("👤 Стефан: stefan@gym.com / stefan123");
        System.out.println("=====================================");
    }
}