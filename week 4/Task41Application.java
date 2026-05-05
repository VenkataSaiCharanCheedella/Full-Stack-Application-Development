// ═══════════════════════════════════════════════════════════════════
//  TASK 4.1 — Basic Servlet-based Spring Boot Web Application
//  File: Task41Application.java
//
//  HOW TO RUN:
//  1. Create Spring Boot project with dependencies:
//       - spring-boot-starter-web
//       - spring-boot-devtools
//  2. Add to src/main/resources/application.properties:
//       server.port=8085
//  3. Run: mvn spring-boot:run  OR  run Task41Application.java
//  4. Visit: http://localhost:8085/hello
//             http://localhost:8085/info
// ═══════════════════════════════════════════════════════════════════

package com.week4.task41;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// ── Entry point ──────────────────────────────────────────────────────────────
// @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
// Spring Boot auto-configures an embedded Tomcat servlet container on startup.
@SpringBootApplication
public class Task41Application {

    public static void main(String[] args) {
        // Launches embedded Tomcat — no external server needed
        SpringApplication.run(Task41Application.class, args);
        System.out.println("✅ Task 4.1 — Embedded Tomcat started on port 8085");
        System.out.println("   Visit: http://localhost:8085/hello");
    }
}

// ── REST Controller — verifies servlet behavior ───────────────────────────────
// @RestController = @Controller + @ResponseBody
// Every method return value is written directly to the HTTP response body.
@RestController
class HelloController {

    // Maps GET /hello
    @GetMapping("/hello")
    public String hello() {
        return "Hello from Spring Boot Servlet Container! Tomcat is running.";
    }

    // Maps GET /info — shows server info
    @GetMapping("/info")
    public String info() {
        return "Spring Boot App | Embedded Tomcat | Port: 8085 | Servlet-based Architecture";
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  application.properties  (put this in src/main/resources/)
// ─────────────────────────────────────────────────────────────────────────────
// server.port=8085
// spring.application.name=task41-servlet-app
// ─────────────────────────────────────────────────────────────────────────────
