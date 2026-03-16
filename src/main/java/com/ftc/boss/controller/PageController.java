package com.ftc.boss.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Paths;

@Controller
public class PageController {

    @Value("${app.profile.dir:uploads/profiles}")
    private String profileDir;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    // Root goes to login
    @GetMapping("/")
    public String root() { return "forward:/login.html"; }

    @GetMapping("/login")
    public String login() { return "forward:/login.html"; }

    @GetMapping("/portal")
    public String portal() { return "forward:/index.html"; }

    @GetMapping("/admin")
    public String admin() { return "forward:/admin.html"; }

    @GetMapping("/reset-password")
    public String reset() { return "forward:/reset-password.html"; }

    // Serve profile photos
    @GetMapping("/profiles/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveProfile(@PathVariable String filename) {
        try {
            Resource r = new FileSystemResource(Paths.get(profileDir).resolve(filename));
            if (!r.exists()) return ResponseEntity.notFound().build();
            String ct = filename.toLowerCase().endsWith(".png") ? "image/png" : "image/jpeg";
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(ct)).body(r);
        } catch (Exception e) { return ResponseEntity.notFound().build(); }
    }

    // Serve uploaded research documents
    @GetMapping("/doc/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveDoc(@PathVariable String filename) {
        try {
            Resource r = new FileSystemResource(Paths.get(uploadDir).resolve(filename));
            if (!r.exists()) return ResponseEntity.notFound().build();
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "inline; filename=\"" + filename + "\"")
                .body(r);
        } catch (Exception e) { return ResponseEntity.notFound().build(); }
    }
}
