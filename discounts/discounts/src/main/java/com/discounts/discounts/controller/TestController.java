package com.discounts.discounts.controller;

import com.discounts.discounts.entity.TestEntity;
import com.discounts.discounts.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class TestController {

    @Autowired
    private TestRepository testRepository;

    @GetMapping("/test")
    public Map<String, String> testConnection() {
        // Try to query the database to verify connection
        try {
            List<TestEntity> entities = testRepository.findAll();
            return Map.of("status", "success", "message", "Database connection established successfully");
        } catch (Exception e) {
            return Map.of("status", "error", "message", "Database connection failed: " + e.getMessage());
        }
    }

    @PostMapping("/test")
    public TestEntity createTest(@RequestBody TestEntity test) {
        return testRepository.save(test);
    }
} 