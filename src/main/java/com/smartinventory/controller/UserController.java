package com.smartinventory.controller;

import com.smartinventory.entity.User;
import com.smartinventory.enums.Role;
import com.smartinventory.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // =========================================================
    // ADMIN - REGISTER USER
    // =========================================================

    @PostMapping("/register")
    public User registerUser(
            @RequestBody User user) {

        return userService.registerUser(user);
    }

    // =========================================================
    // PUBLIC - CREATE ACCOUNT
    // =========================================================
    //
    // Public users are ALWAYS EMPLOYEE.
    // They cannot select ADMIN or MANAGER.
    //
    // =========================================================

    @PostMapping("/public-register")
    public User publicRegisterUser(
            @RequestBody User user) {

        user.setRole(Role.EMPLOYEE);

        return userService.registerUser(user);
    }

    // =========================================================
    // LOGIN
    // =========================================================

    @PostMapping("/login")
    public Map<String, String> loginUser(
            @RequestBody User user) {

        User authenticatedUser =
                userService.authenticateUser(
                        user.getUsername(),
                        user.getPassword()
                );

        String token =
                userService.loginUser(
                        user.getUsername(),
                        user.getPassword()
                );

        Map<String, String> response =
                new HashMap<>();

        response.put(
                "token",
                token
        );

        response.put(
                "username",
                authenticatedUser.getUsername()
        );

        response.put(
                "fullName",
                authenticatedUser.getFullName()
        );

        response.put(
                "role",
                authenticatedUser.getRole().name()
        );

        return response;
    }

    // =========================================================
    // FORGOT PASSWORD - SEND OTP
    // =========================================================

    @PostMapping("/forgot-password")
    public Map<String, String> forgotPassword(
            @RequestBody Map<String, String> request) {

        String email =
                request.get("email");

        userService.sendPasswordResetOtp(email);

        Map<String, String> response =
                new HashMap<>();

        response.put(
                "message",
                "OTP has been sent to your email"
        );

        return response;
    }

    // =========================================================
    // VERIFY OTP
    // =========================================================

    @PostMapping("/verify-otp")
    public Map<String, Object> verifyOtp(
            @RequestBody Map<String, String> request) {

        String email =
                request.get("email");

        String otp =
                request.get("otp");

        boolean verified =
                userService.verifyPasswordResetOtp(
                        email,
                        otp
                );

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "verified",
                verified
        );

        response.put(
                "message",
                "OTP verified successfully"
        );

        return response;
    }

    // =========================================================
    // RESET PASSWORD
    // =========================================================

    @PostMapping("/reset-password")
    public Map<String, String> resetPassword(
            @RequestBody Map<String, String> request) {

        String email =
                request.get("email");

        String newPassword =
                request.get("newPassword");

        userService.resetPassword(
                email,
                newPassword
        );

        Map<String, String> response =
                new HashMap<>();

        response.put(
                "message",
                "Password reset successfully"
        );

        return response;
    }

    // =========================================================
    // GET ALL USERS - ADMIN
    // =========================================================

    @GetMapping
    public List<User> getAllUsers() {

        return userService.getAllUsers();
    }

    // =========================================================
    // GET USER BY ID - ADMIN
    // =========================================================

    @GetMapping("/{id}")
    public User getUserById(
            @PathVariable Long id) {

        return userService.getUserById(id);
    }

    // =========================================================
    // DELETE USER - ADMIN
    // =========================================================

    @DeleteMapping("/{id}")
    public String deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);

        return "User deleted successfully";
    }
}