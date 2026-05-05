// ═══════════════════════════════════════════════════════════════════
//  WEEK 5 — SESSION 21
//  Task: Create Spring Boot project + Configure JDBC Connection
//  File: Session21_JdbcConfig.java
//
//  pom.xml dependencies needed:
//    - spring-boot-starter-web
//    - spring-boot-starter-jdbc
//    - mysql-connector-java  (or h2 for in-memory testing)
//
//  application.properties:
//    spring.datasource.url=jdbc:mysql://localhost:3306/studentdb
//    spring.datasource.username=root
//    spring.datasource.password=root
//    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
//
//  Test: GET http://localhost:8085/db/test
//        GET http://localhost:8085/db/version
// ═══════════════════════════════════════════════════════════════════

package com.week5.session21;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// ── Entry Point ───────────────────────────────────────────────────────────────
@SpringBootApplication
public class Session21_JdbcConfig implements CommandLineRunner {

    // JdbcTemplate — Spring's JDBC helper class
    // Auto-configured from application.properties datasource settings
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(Session21_JdbcConfig.class, args);
    }

    // Runs after startup — verifies JDBC connection
    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n" + "═".repeat(50));
        System.out.println("  Session 21 — JDBC Connection Test");
        System.out.println("═".repeat(50));
        try {
            // Simple query to check connection
            String dbVersion = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
            System.out.println("  ✅ JDBC Connected! DB Version: " + dbVersion);
        } catch (Exception e) {
            System.out.println("  ⚠️  DB not reachable — using H2 in-memory fallback.");
            System.out.println("     Error: " + e.getMessage());
        }
        System.out.println("  Test: GET http://localhost:8085/db/test");
        System.out.println("═".repeat(50) + "\n");
    }
}

// ── Controller to verify JDBC from REST endpoint ──────────────────────────────
@RestController
@RequestMapping("/db")
class JdbcTestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // GET /db/test — confirms JDBC datasource is configured and reachable
    @GetMapping("/test")
    public String testConnection() {
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return "✅ JDBC Connection SUCCESS — SELECT 1 returned: " + result;
        } catch (Exception e) {
            return "❌ JDBC Connection FAILED: " + e.getMessage();
        }
    }

    // GET /db/version — returns database version
    @GetMapping("/version")
    public String dbVersion() {
        try {
            String version = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
            return "Database Version: " + version;
        } catch (Exception e) {
            return "Could not fetch version: " + e.getMessage();
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  application.properties (src/main/resources/)
// ─────────────────────────────────────────────────────────────────────────────
//
//  # MySQL
//  spring.datasource.url=jdbc:mysql://localhost:3306/studentdb
//  spring.datasource.username=root
//  spring.datasource.password=root
//  spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
//
//  # OR H2 in-memory (no MySQL needed — good for quick testing)
//  spring.datasource.url=jdbc:h2:mem:studentdb
//  spring.datasource.driver-class-name=org.h2.Driver
//  spring.datasource.username=sa
//  spring.datasource.password=
//  spring.h2.console.enabled=true
//  spring.h2.console.path=/h2-console
//
//  server.port=8085
// ─────────────────────────────────────────────────────────────────────────────
