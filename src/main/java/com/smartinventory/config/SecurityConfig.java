package com.smartinventory.config;

import com.smartinventory.security.JwtAuthenticationFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // =========================================================
    // SECURITY FILTER CHAIN
    // =========================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                // =================================================
                // CORS
                // =================================================

                .cors(Customizer.withDefaults())

                // =================================================
                // CSRF
                // =================================================

                .csrf(csrf -> csrf.disable())

                // =================================================
                // AUTHORIZATION
                // =================================================

                .authorizeHttpRequests(auth -> auth

                        // -----------------------------------------
                        // CORS PREFLIGHT
                        // -----------------------------------------

                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        // -----------------------------------------
                        // PUBLIC AUTHENTICATION
                        // -----------------------------------------

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/users/login",
                                "/api/users/register",
                                "/api/users/public-register",
                                "/api/users/forgot-password",
                                "/api/users/verify-otp",
                                "/api/users/reset-password"
                        ).permitAll()

                        // -----------------------------------------
                        // DASHBOARD
                        // -----------------------------------------

                        .requestMatchers(
                                "/api/dashboard/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "MANAGER",
                                "EMPLOYEE"
                        )

                        // -----------------------------------------
                        // REPORTS
                        // -----------------------------------------

                        .requestMatchers(
                                "/api/reports/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "MANAGER"
                        )

                        // -----------------------------------------
                        // USERS
                        // ADMIN ONLY
                        // -----------------------------------------

                        .requestMatchers(
                                "/api/users/**"
                        ).hasRole("ADMIN")

                        // -----------------------------------------
                        // SUPPLIERS
                        // -----------------------------------------

                        .requestMatchers(
                                "/api/suppliers/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "MANAGER"
                        )

                        // -----------------------------------------
                        // WAREHOUSES
                        // -----------------------------------------

                        .requestMatchers(
                                "/api/warehouses/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "MANAGER"
                        )

                        // -----------------------------------------
                        // PURCHASE ORDERS
                        // -----------------------------------------

                        .requestMatchers(
                                "/api/purchaseorders/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "MANAGER"
                        )

                        // -----------------------------------------
                        // PRODUCTS
                        // -----------------------------------------

                        .requestMatchers(
                                "/api/products/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "MANAGER",
                                "EMPLOYEE"
                        )

                        // -----------------------------------------
                        // INVENTORY
                        // -----------------------------------------

                        .requestMatchers(
                                "/api/inventories/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "MANAGER",
                                "EMPLOYEE"
                        )

                        // -----------------------------------------
                        // SALES ORDERS
                        // -----------------------------------------

                        .requestMatchers(
                                "/api/salesorders/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "MANAGER",
                                "EMPLOYEE"
                        )

                        // -----------------------------------------
                        // EVERYTHING ELSE
                        // -----------------------------------------

                        .anyRequest().authenticated()
                )

                // =================================================
                // JWT FILTER
                // =================================================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    // =========================================================
    // CORS CONFIGURATION
    // =========================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        // React/Vite frontend
        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173"
                )
        );

        // HTTP methods
        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        // Request headers
        configuration.setAllowedHeaders(
                List.of("*")
        );

        // Allow Authorization header / credentials
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}