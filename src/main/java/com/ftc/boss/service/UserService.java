package com.ftc.boss.service;

import com.ftc.boss.model.AppUser;
import com.ftc.boss.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private EmailService emailService;

    @Value("${app.profile.dir:uploads/profiles}")
    private String profileDir;

    // SHA-256 hash - no external library needed
    public static String hash(String password) {
        try {
            MessageDigest d = MessageDigest.getInstance("SHA-256");
            byte[] h = d.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : h) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    // FEATURE 1 & 2: Register - saves name, email, hashed password, role, dept to DB
    public AppUser register(String fullName, String email, String password, String role, String dept) {
        if (repo.existsByEmail(email.trim().toLowerCase()))
            throw new RuntimeException("This email is already registered. Please login.");
        AppUser u = new AppUser();
        u.setFullName(fullName.trim());
        u.setEmail(email.trim().toLowerCase());
        u.setPasswordHash(hash(password));
        u.setRole(role != null ? role.toUpperCase() : "STUDENT");
        u.setDepartment(dept != null ? dept.trim() : "");
        repo.save(u);
        System.out.println("[DB] New user registered: " + email);
        return u;
    }

    // FEATURE 1: Login - checks email + password from DB
    public Optional<AppUser> login(String email, String password) {
        return repo.findByEmail(email.trim().toLowerCase())
            .filter(u -> u.getPasswordHash().equals(hash(password)));
    }

    // FEATURE 4: Upload profile photo - filename stored in DB
    public AppUser uploadPhoto(Long userId, MultipartFile photo) throws Exception {
        AppUser u = repo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found."));
        String orig = photo.getOriginalFilename() != null ? photo.getOriginalFilename() : "photo.jpg";
        String ext = orig.contains(".") ? orig.substring(orig.lastIndexOf('.')) : ".jpg";
        ext = ext.toLowerCase();
        if (!ext.equals(".jpg") && !ext.equals(".jpeg") && !ext.equals(".png") && !ext.equals(".gif"))
            throw new RuntimeException("Only JPG, PNG or GIF images allowed.");
        String filename = "user_" + userId + "_" + System.currentTimeMillis() + ext;
        Path dir = Paths.get(profileDir);
        if (!Files.exists(dir)) Files.createDirectories(dir);
        Files.copy(photo.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        u.setProfilePhoto(filename);
        repo.save(u);
        System.out.println("[DB] Profile photo saved for user: " + userId);
        return u;
    }

    // FEATURE 2: Forgot password - reset token stored in DB
    public boolean forgotPassword(String email) {
        Optional<AppUser> opt = repo.findByEmail(email.trim().toLowerCase());
        if (opt.isEmpty()) return false;
        AppUser u = opt.get();
        String token = UUID.randomUUID().toString().replace("-", "");
        u.setResetToken(token);
        u.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        repo.save(u);
        emailService.sendPasswordReset(u.getEmail(), u.getFullName(), token);
        return true;
    }

    // FEATURE 2: Reset password using token
    public boolean resetPassword(String token, String newPassword) {
        Optional<AppUser> opt = repo.findByResetToken(token);
        if (opt.isEmpty()) return false;
        AppUser u = opt.get();
        if (u.getResetTokenExpiry() == null || u.getResetTokenExpiry().isBefore(LocalDateTime.now()))
            return false;
        u.setPasswordHash(hash(newPassword));
        u.setResetToken(null);
        u.setResetTokenExpiry(null);
        repo.save(u);
        return true;
    }

    public List<AppUser> getAll() { return repo.findAll(); }
    public Optional<AppUser> getById(Long id) { return repo.findById(id); }
    public long count() { return repo.count(); }
}
