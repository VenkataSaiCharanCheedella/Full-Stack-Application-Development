// ═══════════════════════════════════════════════════════════════════
//  TASK 4.2 — @Autowired Field Injection: Controller → Service
//  File: Task42Application.java
//
//  HOW TO RUN:
//  1. Run Task42Application.main()
//  2. Test endpoints:
//       GET http://localhost:8085/greet
//       GET http://localhost:8085/greet/John
// ═══════════════════════════════════════════════════════════════════

package com.week4.task42;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

// ── Entry point ───────────────────────────────────────────────────────────────
@SpringBootApplication
public class Task42Application {
    public static void main(String[] args) {
        SpringApplication.run(Task42Application.class, args);
        System.out.println("✅ Task 4.2 — Field Injection started on port 8085");
    }
}

// ── Service Layer ─────────────────────────────────────────────────────────────
// @Service marks this as a Spring-managed bean.
// Spring will create one instance and keep it in the ApplicationContext.
@Service
class GreetingService {

    public String greet(String name) {
        return "Hello, " + name + "! Welcome to Spring Boot with @Autowired field injection.";
    }

    public String defaultGreet() {
        return "Hello, World! Service is working correctly.";
    }
}

// ── Controller Layer ──────────────────────────────────────────────────────────
// @Autowired at FIELD LEVEL:
// Spring injects the GreetingService bean directly into this field.
// No constructor or setter needed — Spring uses reflection.
@RestController
class GreetingController {

    // ★ Field Injection — Spring injects GreetingService here automatically
    @Autowired
    private GreetingService greetingService;

    // GET /greet  — calls service default method
    @GetMapping("/greet")
    public String greet() {
        return greetingService.defaultGreet();
    }

    // GET /greet/{name}  — calls service with dynamic name
    @GetMapping("/greet/{name}")
    public String greetByName(@PathVariable String name) {
        return greetingService.greet(name);
    }
}
