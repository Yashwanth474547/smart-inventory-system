package com.smartinventory.service;

import com.smartinventory.entity.User;

import java.util.List;

public interface UserService {

    User registerUser(User user);

    User authenticateUser(String identifier, String password);

    String loginUser(String identifier, String password);

    List<User> getAllUsers();

    User getUserById(Long id);

    void deleteUser(Long id);

    // Forgot password
    void sendPasswordResetOtp(String email);

    // Verify OTP
    boolean verifyPasswordResetOtp(String email, String otp);

    // Reset password
    void resetPassword(String email, String newPassword);
}