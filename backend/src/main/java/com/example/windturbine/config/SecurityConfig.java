package com.example.windturbine.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Health probe
                        .requestMatchers("/actuator/health").permitAll()

                        // Operations / Supervisors / Analysts / Maintenance / Reliability / Admin
                        .requestMatchers("/api/monitoring/**")
                        .hasAnyRole("OPS", "SUPERVISOR", "ANALYST", "MAINT", "RELIABILITY", "ADMIN")

                        // Telemetry ingest (OPS, ADMIN)
                        .requestMatchers("/api/telemetry/**")
                        .hasAnyRole("OPS", "ADMIN")

                        // Reporting / Data analysis (ANALYST, MAINT, SUPERVISOR, ADMIN)
                        .requestMatchers("/api/reporting/**")
                        .hasAnyRole("ANALYST", "MAINT", "SUPERVISOR", "ADMIN")

                        // Admin / platform endpoints (CRUD, jobs, users)
                        .requestMatchers("/api/admin/**", "/actuator/**", "/api/jobs/**", "/api/users/**")
                        .hasRole("ADMIN")

                        // anything else must be authenticated
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults()); // HTTP Basic login

        return http.build();
    }

    // Load users + roles from DB tables: users + authorities
    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }
}