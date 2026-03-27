// ═══════════════════════════════════════════════════════════════════
//  TASK 4.4 — @Qualifier to resolve multiple bean ambiguity
//             NotificationService → EmailNotificationService
//                                → SMSNotificationService
//
//  TASK 4.5 — @Autowired(required=false) Optional Dependency
//             AnalyticsService — may or may not be present
//  File: Task44_45Application.java
//
//  HOW TO RUN:
//  1. Run Task44_45Application.main()
//  2. Test endpoints:
//       GET http://localhost:8085/notify/email
//       GET http://localhost:8085/notify/sms
//       GET http://localhost:8085/notify/both
//       GET http://localhost:8085/notify/analytics   ← optional bean demo
// ═══════════════════════════════════════════════════════════════════

package com.week4.task44_45;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// ── Entry point ───────────────────────────────────────────────────────────────
@SpringBootApplication
public class Task44_45Application {
    public static void main(String[] args) {
        SpringApplication.run(Task44_45Application.class, args);
        System.out.println("✅ Task 4.4 & 4.5 — Qualifier + Optional Injection started");
    }
}

// ════════════════════════════════════════════════════════════════════
//  TASK 4.4 — @Qualifier
// ════════════════════════════════════════════════════════════════════

// ── Interface: NotificationService ───────────────────────────────────────────
interface NotificationService {
    String send(String message);
}

// ── Implementation 1: EmailNotificationService ───────────────────────────────
// @Service("emailService") gives this bean the qualifier name "emailService"
@Service("emailService")
class EmailNotificationService implements NotificationService {

    @Override
    public String send(String message) {
        return "📧 EMAIL sent: [" + message + "] → user@example.com";
    }
}

// ── Implementation 2: SMSNotificationService ─────────────────────────────────
// @Service("smsService") gives this bean the qualifier name "smsService"
@Service("smsService")
class SMSNotificationService implements NotificationService {

    @Override
    public String send(String message) {
        return "📱 SMS sent:   [" + message + "] → +91-98765-43210";
    }
}

// ── Controller using @Qualifier ───────────────────────────────────────────────
// Problem without @Qualifier:
//   Spring finds TWO beans matching NotificationService → throws NoUniqueBeanDefinitionException
// Solution:
//   @Qualifier("emailService") → use exactly this bean
//   @Qualifier("smsService")   → use exactly this bean
@RestController
@RequestMapping("/notify")
class NotificationController {

    // ★ @Qualifier("emailService") — Spring picks EmailNotificationService
    @Autowired
    @Qualifier("emailService")
    private NotificationService emailService;

    // ★ @Qualifier("smsService") — Spring picks SMSNotificationService
    @Autowired
    @Qualifier("smsService")
    private NotificationService smsService;

    // ── TASK 4.5 — Optional dependency injected here too ─────────────────────
    // ★ required=false — if AnalyticsService bean does NOT exist, Spring
    //   injects null instead of throwing NoSuchBeanDefinitionException.
    //   This field will be null if the bean is commented out below.
    @Autowired(required = false)
    private AnalyticsService analyticsService;   // optional — may be null

    // GET /notify/email
    @GetMapping("/email")
    public String notifyEmail() {
        return emailService.send("Your order has been confirmed!");
    }

    // GET /notify/sms
    @GetMapping("/sms")
    public String notifySms() {
        return smsService.send("OTP: 482910 — valid for 10 mins.");
    }

    // GET /notify/both — uses both implementations
    @GetMapping("/both")
    public String notifyBoth() {
        return emailService.send("Invoice ready") + "\n" +
               smsService.send("Invoice ready");
    }

    // ── TASK 4.5 — GET /notify/analytics ─────────────────────────────────────
    @GetMapping("/analytics")
    public String analytics() {
        // ★ Null check — gracefully handle absent optional bean
        if (analyticsService == null) {
            return "ℹ️  AnalyticsService is NOT present (optional bean is null). " +
                   "Application continues to work normally without it.";
        }
        return analyticsService.track("NotificationController.analytics called");
    }
}

// ════════════════════════════════════════════════════════════════════
//  TASK 4.5 — Optional Bean
//  ★ To test null scenario: comment out @Component below and restart.
//    The /notify/analytics endpoint will return the null-handled message.
// ════════════════════════════════════════════════════════════════════

// @Component  ← Comment this out to simulate "bean not present" scenario
@Component
class AnalyticsService {

    public String track(String event) {
        return "📊 Analytics tracked: [" + event + "] at " + java.time.LocalDateTime.now();
    }
}
