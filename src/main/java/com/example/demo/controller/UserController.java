package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.UUID;

@Controller
public class UserController {

    private final UserRepository repo;
    private final BCryptPasswordEncoder encoder;

    @Value("${upload.path}")
    private String uploadDir;

    public UserController(UserRepository repo, BCryptPasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @GetMapping("/")
    public String indexPage(Model model) {
        return "index";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, BindingResult result) {
        if (result.hasErrors()) return "register";
        user.setPassword(encoder.encode(user.getPassword()));
        repo.save(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        User user = repo.findByEmail(principal.getName()).orElseThrow();
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @ModelAttribute User form,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
            Principal principal) {

        User user = repo.findByEmail(principal.getName()).orElseThrow();

        user.setName(form.getName());
        user.setEmail(form.getEmail());
        user.setPhone(form.getPhone());
        user.setGender(form.getGender());
        user.setAge(form.getAge());
        user.setRole(form.getRole());
        user.setBio(form.getBio());

        // ✅ Handle file upload
        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                // Create uploads directory if not exists
                File uploadFolder = new File(uploadDir);
                if (!uploadFolder.exists()) {
                    uploadFolder.mkdirs();
                }

                // Generate unique file name
                String fileName = UUID.randomUUID() + "_" + avatarFile.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, fileName);

                // Save file to local disk
                Files.copy(avatarFile.getInputStream(), filePath);

                // Store relative path (for Thymeleaf display)
                user.setAvatar("/uploads/" + fileName);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        repo.save(user);
        return "redirect:/profile";
    }

    @PostMapping("/profile/delete")
    public String deleteProfile(Principal principal) {
        User user = repo.findByEmail(principal.getName()).orElseThrow();
        repo.delete(user);
        return "redirect:/logout";
    }
}
