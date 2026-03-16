package com.ftc.boss.controller;

import com.ftc.boss.model.ApiResult;
import com.ftc.boss.model.AppUser;
import com.ftc.boss.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService svc;

    // FEATURE 1 & 2: Register new user - saved to DB
    @PostMapping("/register")
    public ResponseEntity<ApiResult> register(
            @RequestParam("fullName")   String fullName,
            @RequestParam("email")      String email,
            @RequestParam("password")   String password,
            @RequestParam(value="role",       required=false, defaultValue="STUDENT") String role,
            @RequestParam(value="department", required=false, defaultValue="")        String dept) {

        if (blank(fullName)) return bad("Full name is required.");
        if (blank(email))    return bad("Email is required.");
        if (!email.contains("@")) return bad("Please enter a valid email address.");
        if (blank(password)) return bad("Password is required.");
        if (password.length() < 6) return bad("Password must be at least 6 characters.");

        try {
            AppUser u = svc.register(fullName, email, password, role, dept);
            return ok("Registration successful! You can now login.", safe(u));
        } catch (RuntimeException e) {
            return bad(e.getMessage());
        }
    }

    // FEATURE 1: Login - checks credentials against DB
    @PostMapping("/login")
    public ResponseEntity<ApiResult> login(
            @RequestParam("email")    String email,
            @RequestParam("password") String password) {

        if (blank(email) || blank(password)) return bad("Email and password are required.");
        Optional<AppUser> opt = svc.login(email, password);
        if (opt.isEmpty())
            return ResponseEntity.status(401)
                .body(new ApiResult(false, "Incorrect email or password."));
        return ok("Login successful!", safe(opt.get()));
    }

    // FEATURE 4: Upload profile photo - stored in DB
    @PostMapping("/upload-photo/{userId}")
    public ResponseEntity<ApiResult> uploadPhoto(
            @PathVariable Long userId,
            @RequestParam("photo") MultipartFile photo) {
        if (photo == null || photo.isEmpty()) return bad("Please choose an image file.");
        try {
            AppUser u = svc.uploadPhoto(userId, photo);
            return ok("Profile photo updated!", safe(u));
        } catch (RuntimeException e) {
            return bad(e.getMessage());
        } catch (Exception e) {
            return bad("Upload failed: " + e.getMessage());
        }
    }

    // FEATURE 2: Forgot password
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResult> forgot(@RequestParam("email") String email) {
        if (blank(email)) return bad("Email is required.");
        boolean sent = svc.forgotPassword(email);
        if (!sent) return bad("No account found with this email.");
        return ok("Password reset link sent to " + email + ". Please check your inbox.", null);
    }

    // FEATURE 2: Reset password with token
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResult> reset(
            @RequestParam("token")       String token,
            @RequestParam("newPassword") String newPassword) {
        if (blank(token) || blank(newPassword)) return bad("Token and new password required.");
        if (newPassword.length() < 6) return bad("Password must be at least 6 characters.");
        boolean done = svc.resetPassword(token, newPassword);
        if (!done) return bad("Reset link is invalid or expired.");
        return ok("Password reset successful! Please login.", null);
    }

    // Admin: get all users
    @GetMapping("/users")
    public ResponseEntity<ApiResult> users() {
        List<Map<String,Object>> list = new ArrayList<>();
        for (AppUser u : svc.getAll()) list.add(safe(u));
        return ok("Success", list);
    }

    // Build safe user map (no password hash)
    private Map<String,Object> safe(AppUser u) {
        Map<String,Object> m = new LinkedHashMap<>();
        m.put("id",           u.getId());
        m.put("fullName",     u.getFullName());
        m.put("email",        u.getEmail());
        m.put("role",         u.getRole());
        m.put("department",   u.getDepartment() != null ? u.getDepartment() : "");
        m.put("profilePhoto", u.getProfilePhoto() != null ? "/profiles/" + u.getProfilePhoto() : null);
        m.put("createdAt",    u.getFormattedDate());
        return m;
    }

    private ResponseEntity<ApiResult> ok(String msg, Object data) {
        return ResponseEntity.ok(new ApiResult(true, msg, data));
    }
    private ResponseEntity<ApiResult> bad(String msg) {
        return ResponseEntity.badRequest().body(new ApiResult(false, msg));
    }
    private boolean blank(String s) { return s == null || s.trim().isEmpty(); }
}
