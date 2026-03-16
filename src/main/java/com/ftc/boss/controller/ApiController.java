package com.ftc.boss.controller;

import com.ftc.boss.model.ApiResult;
import com.ftc.boss.model.Submission;
import com.ftc.boss.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiController {

    @Autowired
    private SubmissionService svc;

    // FEATURE 3: Submit research form - save to DB + email admin + auto reply to user
    @PostMapping("/submit")
    public ResponseEntity<ApiResult> submit(
            @RequestParam("fullName")                                       String fullName,
            @RequestParam("email")                                          String email,
            @RequestParam("paperId")                                        String paperId,
            @RequestParam("paperTitle")                                     String paperTitle,
            @RequestParam("department")                                     String department,
            @RequestParam(value="authorType",   required=false, defaultValue="") String authorType,
            @RequestParam("category")                                       String category,
            @RequestParam(value="categoryName", required=false, defaultValue="") String categoryName,
            @RequestParam(value="subType",      required=false, defaultValue="") String subType,
            @RequestParam(value="projLevel",    required=false, defaultValue="") String projLevel,
            @RequestParam(value="projRank",     required=false, defaultValue="") String projRank,
            @RequestParam(value="projPrize",    required=false)             Integer projPrize,
            @RequestParam(value="numCoauthors", required=false)             Integer numCoauthors,
            @RequestParam(value="file",         required=false)             MultipartFile file) {

        if (blank(fullName))   return bad("Full Name is required.");
        if (blank(email))      return bad("Email is required.");
        if (!email.contains("@")) return bad("Enter a valid email.");
        if (blank(paperId))    return bad("Paper/Project ID is required.");
        if (blank(paperTitle)) return bad("Title is required.");
        if (blank(department)) return bad("Department is required.");
        if (blank(category))   return bad("Category is required.");

        try {
            Submission s = svc.save(fullName, email, paperId, paperTitle, department,
                authorType, category, categoryName, subType, projLevel, projRank,
                projPrize, numCoauthors, file);
            return ResponseEntity.ok(new ApiResult(true,
                "Submission saved! ID: #" + s.getId(),
                java.util.Map.of("id", s.getId(), "rewardAmount",
                    s.getRewardAmount() != null ? s.getRewardAmount() : 0,
                    "submittedAt", s.getFormattedDate())));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ApiResult(false, "Error: " + e.getMessage()));
        }
    }

    // Get all submissions
    @GetMapping("/submissions")
    public ResponseEntity<ApiResult> all() {
        return ok(svc.getAll());
    }

    // Get single submission
    @GetMapping("/submissions/{id}")
    public ResponseEntity<ApiResult> byId(@PathVariable Long id) {
        return svc.getById(id)
            .map(s -> ok(s))
            .orElse(ResponseEntity.status(404).body(new ApiResult(false, "Not found: " + id)));
    }

    // Get submissions by user email
    @GetMapping("/my-submissions")
    public ResponseEntity<ApiResult> mine(@RequestParam("email") String email) {
        if (blank(email)) return bad("Email required.");
        return ok(svc.getByEmail(email));
    }

    // FEATURE 5: Admin replies to submission - emails user
    @PostMapping("/admin/reply/{id}")
    public ResponseEntity<ApiResult> reply(
            @PathVariable Long id,
            @RequestParam("replyMessage") String msg,
            @RequestParam(value="status", required=false, defaultValue="REPLIED") String status) {
        if (blank(msg)) return bad("Reply message cannot be empty.");
        try {
            Submission s = svc.adminReply(id, msg, status);
            return ok("Reply sent to " + s.getEmail());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResult(false, e.getMessage()));
        }
    }

    // Stats
    @GetMapping("/stats")
    public ResponseEntity<ApiResult> stats() { return ok(svc.stats()); }

    private ResponseEntity<ApiResult> ok(Object data) {
        return ResponseEntity.ok(new ApiResult(true, "Success", data));
    }
    private ResponseEntity<ApiResult> bad(String msg) {
        return ResponseEntity.badRequest().body(new ApiResult(false, msg));
    }
    private boolean blank(String s) { return s == null || s.trim().isEmpty(); }
}
