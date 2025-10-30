package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Controller
public class UserController {
    private final UserRepository repo;
    private final BCryptPasswordEncoder encoder;

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
    public String register(@Valid @ModelAttribute User user, BindingResult result) {
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
    public String updateProfile(@ModelAttribute User form, Principal principal) {
        User user = repo.findByEmail(principal.getName()).orElseThrow();
        user.setName(form.getName());
        user.setEmail(form.getEmail());
        user.setPhone(form.getPhone());
        user.setGender(form.getGender());
        user.setAge(form.getAge());
        user.setRole(form.getRole());
        user.setAvatar(form.getAvatar());
        user.setBio(form.getBio());
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
