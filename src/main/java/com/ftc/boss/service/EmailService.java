package com.ftc.boss.service;

import com.ftc.boss.model.Submission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${app.mail.admin:shivankarvivek44@gmail.com}")
    private String adminEmail;

    @Value("${spring.mail.username:shivankarvivek44@gmail.com}")
    private String fromEmail;

    private void send(String to, String subject, String body) {
        if (!mailEnabled || mailSender == null) {
            System.out.println("[EMAIL DISABLED] To=" + to + " | Subject=" + subject);
            System.out.println("[EMAIL BODY] " + body.substring(0, Math.min(100, body.length())));
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
            System.out.println("[EMAIL SENT] To: " + to);
        } catch (Exception e) {
            System.err.println("[EMAIL ERROR] " + e.getMessage());
        }
    }

    // FEATURE 3: Send email to admin when user submits a form
    public void sendToAdmin(Submission s) {
        String reward = (s.getRewardAmount() != null && s.getRewardAmount() > 0)
            ? "Rs." + s.getRewardAmount() + "/-" : "As per policy";
        String body =
            "NEW RESEARCH SUBMISSION - FTC Campus R&D Portal\n" +
            "=================================================\n\n" +
            "Name         : " + s.getFullName() + "\n" +
            "Email        : " + s.getEmail() + "\n" +
            "Paper ID     : " + s.getPaperId() + "\n" +
            "Title        : " + s.getPaperTitle() + "\n" +
            "Department   : " + s.getDepartment() + "\n" +
            "Category     : " + s.getCategoryName() + "\n" +
            "Author Type  : " + s.getAuthorType() + "\n" +
            "Reward       : " + reward + "\n" +
            "Document     : " + (s.getUploadedFile() != null
                ? "http://localhost:8080/doc/" + s.getUploadedFile() : "No document attached") + "\n" +
            "Submitted At : " + s.getFormattedDate() + "\n\n" +
            "Login to Admin Panel: http://localhost:8080/admin.html\n\n" +
            "-- Boss FTC RD Portal";

        send(adminEmail, "New Submission | " + s.getCategoryName() + " | " + s.getFullName(), body);
    }

    // FEATURE 3: Auto-reply to user after submission
    public void sendAutoReplyToUser(Submission s) {
        String rewardLine = "";
        if (s.getRewardAmount() != null && s.getRewardAmount() > 0) {
            rewardLine = "\nYou may be eligible for a research incentive of Rs." +
                s.getRewardAmount() + " as per FTC R&D Policy.\n";
        }
        String body =
            "Dear " + s.getFullName() + ",\n\n" +
            "Thank you for filling your form submission on FTC Campus R&D Portal.\n\n" +
            "We will check your submission and inform you as soon as possible.\n\n" +
            "Submission Details:\n" +
            "-------------------\n" +
            "Paper ID   : " + s.getPaperId() + "\n" +
            "Title      : " + s.getPaperTitle() + "\n" +
            "Category   : " + s.getCategoryName() + "\n" +
            "Author     : " + s.getAuthorType() + "\n" +
            "Department : " + s.getDepartment() + "\n" +
            "Status     : PENDING (under review)\n" +
            rewardLine + "\n" +
            "Regards,\n" +
            "Research & Development Cell\n" +
            "Fabtech Technical Campus, Sangola\n" +
            "Email: shivankarvivek44@gmail.com";

        send(s.getEmail(), "Submission Received - FTC R&D Cell | " + s.getCategoryName(), body);
    }

    // FEATURE 5: Admin sends reply to user
    public void sendAdminReply(Submission s, String replyMsg) {
        String body =
            "Dear " + s.getFullName() + ",\n\n" +
            "The FTC R&D Cell has reviewed your submission and replied:\n\n" +
            "-----------------------------------------------\n" +
            replyMsg + "\n" +
            "-----------------------------------------------\n\n" +
            "Your Submission:\n" +
            "Title    : " + s.getPaperTitle() + "\n" +
            "Category : " + s.getCategoryName() + "\n\n" +
            "Regards,\n" +
            "Research & Development Cell\n" +
            "Fabtech Technical Campus, Sangola\n" +
            "Email: shivankarvivek44@gmail.com";

        send(s.getEmail(),
            "Reply from FTC R&D Cell - " + s.getPaperTitle().substring(0, Math.min(40, s.getPaperTitle().length())),
            body);
    }

    // FEATURE 1: Forgot password reset email
    public void sendPasswordReset(String toEmail, String name, String token) {
        String link = "http://localhost:8080/reset-password.html?token=" + token;
        String body =
            "Dear " + name + ",\n\n" +
            "You requested a password reset for your FTC Campus Portal account.\n\n" +
            "Click the link below (valid for 1 hour):\n" +
            link + "\n\n" +
            "If you did not request this, ignore this email.\n\n" +
            "Regards,\nFTC Campus Portal\nFabtech Technical Campus, Sangola";

        send(toEmail, "Password Reset - FTC Campus Portal", body);
    }
}
