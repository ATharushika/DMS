package com.sdewec.config;

import com.sdewec.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                // Citizen + Volunteer: submit and view own hazard reports
                .requestMatchers(HttpMethod.POST, "/api/hazards").hasAnyRole("CITIZEN", "VOLUNTEER")
                .requestMatchers(HttpMethod.GET, "/api/hazards/my").hasAnyRole("CITIZEN", "VOLUNTEER")
                // Warnings - citizens can view active warnings
                .requestMatchers(HttpMethod.GET, "/api/warnings/active").hasAnyRole(
                    "CITIZEN", "VOLUNTEER", "DUTY_OFFICER", "DMC_OFFICER",
                    "DISTRICT_OFFICER", "RESCUE_TEAM", "SHELTER_COORDINATOR",
                    "RESOURCE_PROVIDER", "ADMIN")
                // Duty Officer: review hazard reports
                .requestMatchers("/api/hazards/{id}/verify", "/api/hazards/{id}/reject",
                                 "/api/hazards/{id}/request-information")
                    .hasRole("DUTY_OFFICER")
                // DMC Officer: warnings
                .requestMatchers(HttpMethod.POST, "/api/warnings").hasRole("DMC_OFFICER")
                .requestMatchers("/api/warnings/{id}/approve", "/api/warnings/{id}/broadcast")
                    .hasAnyRole("DMC_OFFICER", "DISTRICT_OFFICER", "ADMIN")
                // District Officer: incidents, rescue, shelter, resource
                .requestMatchers(HttpMethod.POST, "/api/incidents").hasAnyRole("DISTRICT_OFFICER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/rescue-assignments").hasAnyRole("DISTRICT_OFFICER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/resource-allocations").hasAnyRole("DISTRICT_OFFICER", "ADMIN")
                // Rescue Team: their own assignments
                .requestMatchers("/api/rescue-assignments/{id}/accept",
                                 "/api/rescue-assignments/{id}/status",
                                 "/api/rescue-assignments/{id}/location")
                    .hasAnyRole("RESCUE_TEAM", "ADMIN")
                // Shelter Coordinator: occupancy
                .requestMatchers("/api/shelters/{id}/occupancy").hasAnyRole("SHELTER_COORDINATOR", "DISTRICT_OFFICER", "ADMIN")
                // Resource Provider: update allocation status
                .requestMatchers("/api/resource-allocations/{id}/status")
                    .hasAnyRole("RESOURCE_PROVIDER", "SHELTER_COORDINATOR", "DISTRICT_OFFICER", "ADMIN")
                // Admin: user management
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                .requestMatchers("/api/audit-logs/**").hasRole("ADMIN")
                // All other authenticated endpoints
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
