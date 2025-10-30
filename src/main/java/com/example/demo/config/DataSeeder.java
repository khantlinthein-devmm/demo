package com.example.demo.config;

import com.example.demo.model.Company;
import com.example.demo.model.Job;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.JobRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CompanyRepository companyRepo;
    private final JobRepository jobRepo;

    public DataSeeder(CompanyRepository companyRepo, JobRepository jobRepo) {
        this.companyRepo = companyRepo;
        this.jobRepo = jobRepo;
    }

    @Override
    public void run(String... args) {
        if (companyRepo.count() == 0) {
            Company c1 = new Company();
            c1.setName("TechWave Solutions");
            c1.setLocation("Bangkok");
            c1.setDescription("A fast-growing software company specializing in enterprise solutions.");
            companyRepo.save(c1);

            Company c2 = new Company();
            c2.setName("Bright Hotels Group");
            c2.setLocation("Chiang Mai");
            c2.setDescription("Leading hospitality brand with hotels across Southeast Asia.");
            companyRepo.save(c2);

            Job j1 = new Job();
            j1.setTitle("Backend Developer");
            j1.setDescription("Experience with Spring Boot required.");
            j1.setCompany(c1);
            jobRepo.save(j1);

            Job j2 = new Job();
            j2.setTitle("Hotel Manager");
            j2.setDescription("Strong leadership and guest experience background.");
            j2.setCompany(c2);
            jobRepo.save(j2);
        }
    }
}
