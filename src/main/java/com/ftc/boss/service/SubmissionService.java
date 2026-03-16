package com.ftc.boss.service;

import com.ftc.boss.model.Submission;
import com.ftc.boss.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.*;

@Service
public class SubmissionService {

    @Autowired
    private SubmissionRepository repo;

    @Autowired
    private EmailService emailService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private int calcReward(String cat, String authorType, Integer projPrize) {
        if (projPrize != null && projPrize > 0) return projPrize;
        if (cat == null || authorType == null) return 0;
        boolean is1st = "1st".equals(authorType);
        boolean is2nd = "2nd".equals(authorType);
        switch (cat) {
            case "sci":    return is1st ? 7000 : is2nd ? 3500 : 0;
            case "scopus":
            case "wos":    return is1st ? 5000 : is2nd ? 2500 : 0;
            case "ieee":   return is1st ? 3000 : is2nd ? 1500 : 0;
            case "book":   return is1st ? 1000 : 0;
            default:       return 0;
        }
    }

    // FEATURE 3: Save submission to DB then send emails
    public Submission save(
            String fullName, String email, String paperId, String paperTitle,
            String department, String authorType, String category, String categoryName,
            String subType, String projLevel, String projRank,
            Integer projPrize, Integer numCoauthors, MultipartFile file) throws Exception {

        Submission s = new Submission();
        s.setFullName(fullName.trim());
        s.setEmail(email.trim());
        s.setPaperId(paperId.trim());
        s.setPaperTitle(paperTitle.trim());
        s.setDepartment(department.trim());
        s.setAuthorType(authorType != null ? authorType : "");
        s.setCategory(category);
        s.setCategoryName(categoryName != null ? categoryName : "");
        s.setSubType(subType != null ? subType : "");
        s.setProjLevel(projLevel != null ? projLevel : "");
        s.setProjRank(projRank != null ? projRank : "");
        s.setNumCoauthors(numCoauthors != null ? numCoauthors : 0);
        s.setRewardAmount(calcReward(category, authorType, projPrize));
        s.setStatus("PENDING");

        // FEATURE 5: Save uploaded document file to disk, store filename in DB
        if (file != null && !file.isEmpty()) {
            String orig = file.getOriginalFilename();
            if (orig == null) orig = "document.pdf";
            String safe = orig.replaceAll("[^a-zA-Z0-9._-]", "_");
            String fname = System.currentTimeMillis() + "_" + safe;
            Path dir = Paths.get(uploadDir);
            if (!Files.exists(dir)) Files.createDirectories(dir);
            Files.copy(file.getInputStream(), dir.resolve(fname), StandardCopyOption.REPLACE_EXISTING);
            s.setUploadedFile(fname);
        }

        Submission saved = repo.save(s);
        System.out.println("[DB] Submission saved, ID=" + saved.getId());

        // Send emails in background (FEATURE 3)
        final Submission fs = saved;
        new Thread(() -> {
            emailService.sendToAdmin(fs);
            emailService.sendAutoReplyToUser(fs);
        }).start();

        return saved;
    }

    // FEATURE 5: Admin replies - saves to DB + emails user
    public Submission adminReply(Long id, String replyMsg, String status) {
        Submission s = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Submission not found: " + id));
        s.setAdminReply(replyMsg);
        s.setStatus(status != null ? status : "REPLIED");
        Submission saved = repo.save(s);
        final Submission fs = saved;
        new Thread(() -> emailService.sendAdminReply(fs, replyMsg)).start();
        return saved;
    }

    public List<Submission> getAll() { return repo.findAllOrderByDateDesc(); }
    public Optional<Submission> getById(Long id) { return repo.findById(id); }
    public List<Submission> getByEmail(String email) { return repo.findByEmailOrderBySubmittedAtDesc(email); }

    public Map<String, Long> stats() {
        Map<String, Long> m = new LinkedHashMap<>();
        m.put("total",   repo.count());
        m.put("pending", repo.countByStatus("PENDING"));
        m.put("replied", repo.countByStatus("REPLIED"));
        for (String c : new String[]{"sci","scopus","wos","ieee","conf","patent","proj","book","phd"})
            m.put(c, repo.countByCategory(c));
        return m;
    }
}
