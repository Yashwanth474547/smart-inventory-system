package com.smartinventory.security;

import com.smartinventory.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request) {

        String path = request.getServletPath();

        /*
         * These endpoints are public.
         * JWT authentication must not be required
         * for them.
         */

        return path.equals("/api/users/login")
                || path.equals("/api/users/register")
                || path.equals("/api/users/public-register")
                || path.equals("/api/users/forgot-password")
                || path.equals("/api/users/verify-otp")
                || path.equals("/api/users/reset-password")
                || path.startsWith("/api/auth/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader =
                request.getHeader("Authorization");

        System.out.println(
                "REQUEST: " +
                        request.getRequestURI()
        );

        System.out.println(
                "AUTH HEADER: " +
                        (authHeader != null
                                ? "Bearer token received"
                                : "NO TOKEN")
        );

        /*
         * No JWT token.
         *
         * Do not reject the request here.
         * Spring Security will decide whether
         * the endpoint requires authentication.
         */

        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        String token =
                authHeader
                        .substring(7)
                        .trim();

        try {

            String username =
                    jwtUtil.extractUsername(token);

            String role =
                    jwtUtil.extractRole(token);

            System.out.println(
                    "JWT USER: " +
                            username
            );

            System.out.println(
                    "JWT ROLE: " +
                            role
            );

            /*
             * Validate token.
             */

            if (jwtUtil.validateToken(
                    token,
                    username)) {

                if (role == null
                        || role.isBlank()) {

                    System.out.println(
                            "JWT ROLE IS MISSING"
                    );

                    SecurityContextHolder
                            .clearContext();

                    filterChain.doFilter(
                            request,
                            response
                    );

                    return;
                }

                role = role.trim();

                /*
                 * Avoid ROLE_ROLE_ADMIN.
                 */

                if (role.startsWith("ROLE_")) {

                    role =
                            role.substring(5);
                }

                UsernamePasswordAuthenticationToken
                        authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.singletonList(
                                        new SimpleGrantedAuthority(
                                                "ROLE_" + role
                                        )
                                )
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(
                                authentication
                        );

                System.out.println(
                        "JWT AUTHENTICATION SUCCESS"
                );

                System.out.println(
                        "AUTHORITY: ROLE_" +
                                role
                );

            } else {

                System.out.println(
                        "JWT TOKEN INVALID OR EXPIRED"
                );

                SecurityContextHolder
                        .clearContext();
            }

        } catch (Exception e) {

            System.out.println(
                    "JWT ERROR: " +
                            e.getMessage()
            );

            SecurityContextHolder
                    .clearContext();
        }

        filterChain.doFilter(
                request,
                response
        );
    }
}