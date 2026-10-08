package com.smartinventory.impl;

import com.smartinventory.entity.User;
import com.smartinventory.repository.UserRepository;
import com.smartinventory.service.UserService;
import com.smartinventory.util.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JavaMailSender mailSender;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    private final Random random = new Random();


    // ==========================
    // Register User
    // ==========================

    @Override
    public User registerUser(User user) {

        if (user.getFullName() == null ||
                user.getFullName().isBlank()) {

            throw new RuntimeException("Full name is required");
        }

        if (user.getUsername() == null ||
                user.getUsername().isBlank()) {

            throw new RuntimeException("Username is required");
        }

        if (user.getEmail() == null ||
                user.getEmail().isBlank()) {

            throw new RuntimeException("Email is required");
        }

        if (user.getMobileNumber() == null ||
                user.getMobileNumber().isBlank()) {

            throw new RuntimeException("Mobile number is required");
        }

        if (user.getPassword() == null ||
                user.getPassword().isBlank()) {

            throw new RuntimeException("Password is required");
        }


        // Check duplicate username
        if (userRepository.existsByUsername(
                user.getUsername().trim())) {

            throw new RuntimeException(
                    "Username already exists"
            );
        }


        // Check duplicate email
        if (userRepository.existsByEmail(
                user.getEmail().trim().toLowerCase())) {

            throw new RuntimeException(
                    "Email already exists"
            );
        }


        // Check duplicate mobile
        if (userRepository.existsByMobileNumber(
                user.getMobileNumber().trim())) {

            throw new RuntimeException(
                    "Mobile number already exists"
            );
        }


        user.setFullName(user.getFullName().trim());
        user.setUsername(user.getUsername().trim());
        user.setEmail(user.getEmail().trim().toLowerCase());
        user.setMobileNumber(user.getMobileNumber().trim());


        // Encrypt password before saving
        user.setPassword(
                passwordEncoder.encode(
                        user.getPassword()
                )
        );


        // Reset OTP fields
        user.setResetOtp(null);
        user.setResetOtpExpiry(null);
        user.setResetOtpVerified(false);


        return userRepository.save(user);
    }


    // ==========================
    // Authenticate User
    // ==========================

    @Override
    public User authenticateUser(
            String identifier,
            String password) {

        if (identifier == null ||
                identifier.isBlank() ||
                password == null ||
                password.isBlank()) {

            throw new RuntimeException(
                    "Invalid Username, Email, Mobile Number or Password"
            );
        }


        String loginValue = identifier.trim();

        User user = null;


        // Try Username
        user = userRepository
                .findByUsername(loginValue)
                .orElse(null);


        // Try Email
        if (user == null) {

            user = userRepository
                    .findByEmail(loginValue.toLowerCase())
                    .orElse(null);
        }


        // Try Mobile Number
        if (user == null) {

            user = userRepository
                    .findByMobileNumber(loginValue)
                    .orElse(null);
        }


        if (user == null) {

            throw new RuntimeException(
                    "Invalid Username, Email, Mobile Number or Password"
            );
        }


        /*
         * ==========================================
         * PASSWORD AUTHENTICATION
         * ==========================================
         *
         * New users have BCrypt passwords.
         *
         * Old/test users may temporarily have
         * plain-text passwords.
         *
         * We check the format first so BCrypt
         * never receives a plain-text password.
         */


        String storedPassword = user.getPassword();


        if (storedPassword == null ||
                storedPassword.isBlank()) {

            throw new RuntimeException(
                    "Invalid Username, Email, Mobile Number or Password"
            );
        }


        // ------------------------------------------
        // BCrypt password
        // ------------------------------------------

        boolean isBCryptPassword =
                storedPassword.startsWith("$2a$") ||
                        storedPassword.startsWith("$2b$") ||
                        storedPassword.startsWith("$2y$");


        if (isBCryptPassword) {

            if (passwordEncoder.matches(
                    password,
                    storedPassword)) {

                return user;
            }

        }


        // ------------------------------------------
        // Legacy / plain-text password
        // ------------------------------------------

        if (!isBCryptPassword &&
                password.equals(storedPassword)) {

            /*
             * Convert old plain-text password
             * into BCrypt immediately.
             */

            user.setPassword(
                    passwordEncoder.encode(password)
            );

            userRepository.save(user);

            return user;
        }


        throw new RuntimeException(
                "Invalid Username, Email, Mobile Number or Password"
        );
    }


    // ==========================
    // Login User
    // ==========================

    @Override
    public String loginUser(
            String identifier,
            String password) {

        User user = authenticateUser(
                identifier,
                password
        );

        return jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().name()
        );
    }


    // ==========================
    // Send Password Reset OTP
    // ==========================

    @Override
    public void sendPasswordResetOtp(String email) {

        if (email == null || email.isBlank()) {

            throw new RuntimeException(
                    "Email is required"
            );
        }


        String normalizedEmail =
                email.trim().toLowerCase();


        User user = userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No account found with this email"
                        )
                );


        // Generate 6-digit OTP
        String otp = String.format(
                "%06d",
                random.nextInt(1000000)
        );


        // OTP valid for 10 minutes
        LocalDateTime expiry =
                LocalDateTime.now().plusMinutes(10);


        user.setResetOtp(otp);
        user.setResetOtpExpiry(expiry);
        user.setResetOtpVerified(false);

        userRepository.save(user);


        // Create email
        SimpleMailMessage message =
                new SimpleMailMessage();


        message.setTo(user.getEmail());


        message.setSubject(
                "Smart Inventory - Password Reset OTP"
        );


        message.setText(
                "Hello " + user.getFullName() + ",\n\n" +
                        "Your password reset OTP is: " + otp + "\n\n" +
                        "This OTP is valid for 10 minutes.\n\n" +
                        "If you did not request a password reset, " +
                        "please ignore this email.\n\n" +
                        "Regards,\n" +
                        "Smart Inventory Team"
        );


        mailSender.send(message);
    }


    // ==========================
    // Verify Password Reset OTP
    // ==========================

    @Override
    public boolean verifyPasswordResetOtp(
            String email,
            String otp) {

        if (email == null ||
                email.isBlank() ||
                otp == null ||
                otp.isBlank()) {

            throw new RuntimeException(
                    "Email and OTP are required"
            );
        }


        String normalizedEmail =
                email.trim().toLowerCase();


        User user = userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No account found with this email"
                        )
                );


        if (user.getResetOtp() == null ||
                user.getResetOtpExpiry() == null) {

            throw new RuntimeException(
                    "OTP not requested"
            );
        }


        // Check OTP expiry
        if (LocalDateTime.now()
                .isAfter(user.getResetOtpExpiry())) {

            user.setResetOtp(null);
            user.setResetOtpExpiry(null);
            user.setResetOtpVerified(false);

            userRepository.save(user);

            throw new RuntimeException(
                    "OTP has expired. Please request a new OTP"
            );
        }


        // Check OTP
        if (!user.getResetOtp().equals(otp.trim())) {

            throw new RuntimeException(
                    "Invalid OTP"
            );
        }


        user.setResetOtpVerified(true);

        userRepository.save(user);

        return true;
    }


    // ==========================
    // Reset Password
    // ==========================

    @Override
    public void resetPassword(
            String email,
            String newPassword) {

        if (email == null ||
                email.isBlank()) {

            throw new RuntimeException(
                    "Email is required"
            );
        }


        if (newPassword == null ||
                newPassword.isBlank()) {

            throw new RuntimeException(
                    "New password is required"
            );
        }


        if (newPassword.length() < 6) {

            throw new RuntimeException(
                    "Password must contain at least 6 characters"
            );
        }


        String normalizedEmail =
                email.trim().toLowerCase();


        User user = userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No account found with this email"
                        )
                );


        // User must verify OTP before resetting password
        if (!user.isResetOtpVerified()) {

            throw new RuntimeException(
                    "Please verify the OTP before resetting your password"
            );
        }


        // Encrypt new password
        user.setPassword(
                passwordEncoder.encode(newPassword)
        );


        // Clear OTP information
        user.setResetOtp(null);
        user.setResetOtpExpiry(null);
        user.setResetOtpVerified(false);


        userRepository.save(user);
    }


    // ==========================
    // Get All Users
    // ==========================

    @Override
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }


    // ==========================
    // Get User By ID
    // ==========================

    @Override
    public User getUserById(Long id) {

        return userRepository
                .findById(id)
                .orElse(null);
    }


    // ==========================
    // Delete User
    // ==========================

    @Override
    public void deleteUser(Long id) {

        userRepository.deleteById(id);
    }
}