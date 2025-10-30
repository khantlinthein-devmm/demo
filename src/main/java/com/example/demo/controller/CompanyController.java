package com.example.demo.controller;

import com.example.demo.model.Company;
import com.example.demo.model.JobApplication;
import com.example.demo.model.User;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.JobApplicationRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyRepository companyRepo;
    private final JobApplicationRepository appRepo;
    private final UserRepository userRepo;

    public CompanyController(CompanyRepository companyRepo, JobApplicationRepository appRepo, UserRepository userRepo) {
        this.companyRepo = companyRepo;
        this.appRepo = appRepo;
        this.userRepo = userRepo;
    }

    @GetMapping
    public String listCompanies(Model model) {
        model.addAttribute("companies", companyRepo.findAll());
        return "company-list";
    }

    @GetMapping("/{id}")
    public String companyDetails(@PathVariable Long id, Model model) {
        model.addAttribute("company", companyRepo.findById(id).orElseThrow());
        model.addAttribute("application", new JobApplication());
        return "company-details";
    }

    @PostMapping("/{id}/apply")
    public String applyToCompany(@PathVariable Long id, @ModelAttribute JobApplication form, Principal principal) {
        User user = userRepo.findByEmail(principal.getName()).orElseThrow();
        Company company = companyRepo.findById(id).orElseThrow();

        form.setApplicant(user);
        form.setCompany(company);
        appRepo.save(form);

        return "redirect:/companies";
    }
}
