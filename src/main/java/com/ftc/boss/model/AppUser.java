package com.ftc.boss.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true, length = 200)
    private String email;

    // Password stored as SHA-256 hash in DB
    @Column(name = "password_hash", nullable = false, length = 300)
    private String passwordHash;

    // STUDENT / FACULTY / MANAGEMENT
    @Column(name = "role", length = 50)
    private String role;

    @Column(name = "department", length = 100)
    private String department;

    // Profile photo filename stored in DB
    @Column(name = "profile_photo", length = 300)
    private String profilePhoto;

    // Forgot password token stored in DB
    @Column(name = "reset_token", length = 200)
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.role == null) this.role = "STUDENT";
    }

    public String getFormattedDate() {
        if (createdAt == null) return "";
        return createdAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    public Long getId() { return id; }
    public void setId(Long v) { this.id = v; }
    public String getFullName() { return fullName; }
    public void setFullName(String v) { this.fullName = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String v) { this.passwordHash = v; }
    public String getRole() { return role; }
    public void setRole(String v) { this.role = v; }
    public String getDepartment() { return department; }
    public void setDepartment(String v) { this.department = v; }
    public String getProfilePhoto() { return profilePhoto; }
    public void setProfilePhoto(String v) { this.profilePhoto = v; }
    public String getResetToken() { return resetToken; }
    public void setResetToken(String v) { this.resetToken = v; }
    public LocalDateTime getResetTokenExpiry() { return resetTokenExpiry; }
    public void setResetTokenExpiry(LocalDateTime v) { this.resetTokenExpiry = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
}
