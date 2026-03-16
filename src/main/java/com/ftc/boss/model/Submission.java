package com.ftc.boss.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(name = "email", nullable = false, length = 200)
    private String email;

    @Column(name = "paper_id", nullable = false, length = 100)
    private String paperId;

    @Column(name = "paper_title", nullable = false, length = 600)
    private String paperTitle;

    @Column(name = "department", nullable = false, length = 100)
    private String department;

    @Column(name = "author_type", length = 50)
    private String authorType;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "category_name", length = 200)
    private String categoryName;

    @Column(name = "sub_type", length = 100)
    private String subType;

    @Column(name = "proj_level", length = 100)
    private String projLevel;

    @Column(name = "proj_rank", length = 50)
    private String projRank;

    @Column(name = "reward_amount")
    private Integer rewardAmount;

    @Column(name = "num_coauthors")
    private Integer numCoauthors;

    // Uploaded document filename - stored in DB
    @Column(name = "uploaded_file", length = 500)
    private String uploadedFile;

    // Admin reply message - stored in DB
    @Column(name = "admin_reply", length = 2000)
    private String adminReply;

    // PENDING / REPLIED / APPROVED / REJECTED
    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @PrePersist
    public void prePersist() {
        this.submittedAt = LocalDateTime.now();
        if (this.status == null) this.status = "PENDING";
    }

    public String getFormattedDate() {
        if (submittedAt == null) return "";
        return submittedAt.format(DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a"));
    }

    public Long getId() { return id; }
    public void setId(Long v) { this.id = v; }
    public String getFullName() { return fullName; }
    public void setFullName(String v) { this.fullName = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getPaperId() { return paperId; }
    public void setPaperId(String v) { this.paperId = v; }
    public String getPaperTitle() { return paperTitle; }
    public void setPaperTitle(String v) { this.paperTitle = v; }
    public String getDepartment() { return department; }
    public void setDepartment(String v) { this.department = v; }
    public String getAuthorType() { return authorType; }
    public void setAuthorType(String v) { this.authorType = v; }
    public String getCategory() { return category; }
    public void setCategory(String v) { this.category = v; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String v) { this.categoryName = v; }
    public String getSubType() { return subType; }
    public void setSubType(String v) { this.subType = v; }
    public String getProjLevel() { return projLevel; }
    public void setProjLevel(String v) { this.projLevel = v; }
    public String getProjRank() { return projRank; }
    public void setProjRank(String v) { this.projRank = v; }
    public Integer getRewardAmount() { return rewardAmount; }
    public void setRewardAmount(Integer v) { this.rewardAmount = v; }
    public Integer getNumCoauthors() { return numCoauthors; }
    public void setNumCoauthors(Integer v) { this.numCoauthors = v; }
    public String getUploadedFile() { return uploadedFile; }
    public void setUploadedFile(String v) { this.uploadedFile = v; }
    public String getAdminReply() { return adminReply; }
    public void setAdminReply(String v) { this.adminReply = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime v) { this.submittedAt = v; }
}
