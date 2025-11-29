package com.example.gym.config;

import com.example.gym.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // ===== PUBLIC ENDPOINTS (БЕЗ AUTHENTICATION) =====
                        .requestMatchers("/api/auth/**").permitAll()

                        // Programs - PUBLIC (читање)
                        .requestMatchers("/api/programs").permitAll()
                        .requestMatchers("/api/programs/search").permitAll()

                        // Trainings - PUBLIC (читање)
                        .requestMatchers("/api/trainings").permitAll()

                        // Diets - PUBLIC (читање) 👈
                        .requestMatchers("/api/diets").permitAll()
                        .requestMatchers("/api/diets/search").permitAll()
                        .requestMatchers("/api/diets/{id:[0-9]+}").permitAll() // GET /api/diets/{id}

                        // Days - PUBLIC
                        .requestMatchers("/api/days").permitAll()
                        .requestMatchers("/api/days/**").permitAll()

                        // Videos - PUBLIC
                        .requestMatchers("/api/videos/**").permitAll()

                        // ===== AUTHENTICATED USER ENDPOINTS (САМО ЗА ЛОГИРАНИ) =====
                        // Programs - USER actions
                        .requestMatchers("/api/programs/*/purchase").authenticated()
                        .requestMatchers("/api/programs/my-programs").authenticated()
                        .requestMatchers("/api/programs/*/remaining-days").authenticated()
                        .requestMatchers("/api/programs/*/decrease-days").authenticated()
                        .requestMatchers("/api/programs/*/details").authenticated()

                        // Trainings - USER actions
                        .requestMatchers("/api/trainings/*/purchase").authenticated()
                        .requestMatchers("/api/trainings/my-trainings").authenticated()
                        .requestMatchers("/api/trainings/*/remaining-days").authenticated()
                        .requestMatchers("/api/trainings/*/decrease-days").authenticated()

                        // Diets - USER actions 👈 НОВО
                        .requestMatchers("/api/diets/*/purchase").authenticated()
                        .requestMatchers("/api/diets/my-diets").authenticated()

                        // ===== ADMIN ENDPOINTS (САМО ЗА ADMIN) =====
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // ===== AUTHENTICATED ENDPOINTS (СЀ ОСТАНАТО) =====
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Дозволи requests од React app
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:3001",
                "http://127.0.0.1:3000",
                "http://127.0.0.1:3001"
        ));

        // Дозволи ги сите HTTP методи
        configuration.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS",
                "PATCH"
        ));

        // Дозволи ги сите headers
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Дозволи credentials (JWT tokens)
        configuration.setAllowCredentials(true);

        // Expose Authorization header
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}