package com.smartinventory.config;

import com.smartinventory.security.JwtAuthenticationFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

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

                .cors(cors -> cors.configurationSource(
                        corsConfigurationSource()
                ))

                // =================================================
                // CSRF
                // =================================================

                .csrf(csrf -> csrf.disable())

                // =================================================
                // AUTHORIZATION
                // =================================================

                .authorizeHttpRequests(auth -> auth

                        // -------------------------------------------------
                        // CORS PREFLIGHT
                        // -------------------------------------------------

                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()


                        // -------------------------------------------------
                        // PUBLIC LOGIN
                        // -------------------------------------------------

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/users/login"
                        ).permitAll()


                        // -------------------------------------------------
                        // PUBLIC REGISTRATION
                        // -------------------------------------------------

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/users/register",
                                "/api/users/public-register"
                        ).permitAll()


                        // -------------------------------------------------
                        // FORGOT PASSWORD
                        // -------------------------------------------------

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/users/forgot-password",
                                "/api/users/verify-otp",
                                "/api/users/reset-password"
                        ).permitAll()


                        // -------------------------------------------------
                        // DASHBOARD
                        // -------------------------------------------------

                        .requestMatchers(
                                "/api/dashboard/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "MANAGER",
                                "EMPLOYEE"
                        )


                        // -------------------------------------------------
                        // REPORTS
                        // -------------------------------------------------

                        .requestMatchers(
                                "/api/reports/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "MANAGER"
                        )


                        // -------------------------------------------------
                        // USERS
                        // ADMIN ONLY
                        // -------------------------------------------------

                        .requestMatchers(
                                "/api/users/**"
                        ).hasRole("ADMIN")


                        // -------------------------------------------------
                        // SUPPLIERS
                        // -------------------------------------------------

                        .requestMatchers(
                                "/api/suppliers/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "MANAGER"
                        )


                        // -------------------------------------------------
                        // WAREHOUSES
                        // -------------------------------------------------

                        .requestMatchers(
                                "/api/warehouses/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "MANAGER"
                        )


                        // -------------------------------------------------
                        // PURCHASE ORDERS
                        // -------------------------------------------------

                        .requestMatchers(
                                "/api/purchaseorders/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "MANAGER"
                        )


                        // -------------------------------------------------
                        // PRODUCTS
                        // -------------------------------------------------

                        .requestMatchers(
                                "/api/products/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "MANAGER",
                                "EMPLOYEE"
                        )


                        // -------------------------------------------------
                        // INVENTORY
                        // -------------------------------------------------

                        .requestMatchers(
                                "/api/inventories/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "MANAGER",
                                "EMPLOYEE"
                        )


                        // -------------------------------------------------
                        // SALES ORDERS
                        // -------------------------------------------------

                        .requestMatchers(
                                "/api/salesorders/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "MANAGER",
                                "EMPLOYEE"
                        )


                        // -------------------------------------------------
                        // EVERYTHING ELSE
                        // -------------------------------------------------

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


        // =========================================================
        // ALLOWED FRONTENDS
        // =========================================================

        configuration.setAllowedOrigins(
                List.of(
                        // Local development
                        "http://localhost:5173",
                        "http://localhost:5174",
                        "http://localhost:5175",
                        "http://localhost:5176",
                        "http://localhost:5177",
                        "http://localhost:5178",

                        // Railway production frontend
                        "https://smart-inventory-frontend-production-d849.up.railway.app"
                )
        );


        // =========================================================
        // ALLOWED HTTP METHODS
        // =========================================================

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );


        // =========================================================
        // ALLOWED HEADERS
        // =========================================================

        configuration.setAllowedHeaders(
                List.of("*")
        );


        // =========================================================
        // ALLOW AUTHORIZATION / COOKIES
        // =========================================================

        configuration.setAllowCredentials(true);


        // =========================================================
        // REGISTER CORS
        // =========================================================

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );


        return source;
    }
}