// ═══════════════════════════════════════════════════════════════════
//  WEEK 5 — SESSION 23
//  Task: Student CRUD Application — Create, Read, Update, Delete
//        using Spring Boot + JPA + Relational DB (test via Postman)
//  File: Session23_StudentCRUD.java
//
//  pom.xml dependencies:
//    - spring-boot-starter-data-jpa
//    - spring-boot-starter-web
//    - mysql-connector-java  (or h2 for quick test)
//
//  POSTMAN TEST GUIDE (at bottom of file)
// ═══════════════════════════════════════════════════════════════════

package com.week5.session23;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// ── Entry Point ───────────────────────────────────────────────────────────────
@SpringBootApplication
public class Session23_StudentCRUD implements CommandLineRunner {

    @Autowired
    private StudentCrudService service;

    public static void main(String[] args) {
        SpringApplication.run(Session23_StudentCRUD.class, args);
    }

    @Override
    public void run(String... args) {
        // Pre-load sample data
        service.create(new StudentEntity("Ravi Kumar",   "CSE", 20, "ravi@mail.com"));
        service.create(new StudentEntity("Priya Sharma", "ECE", 21, "priya@mail.com"));
        service.create(new StudentEntity("Arjun Singh",  "CSE", 22, "arjun@mail.com"));
        System.out.println("✅ Session 23 — Student CRUD ready. Test via Postman.");
        System.out.println("   Base URL: http://localhost:8085/students");
    }
}

// ── Entity ────────────────────────────────────────────────────────────────────
@Entity
@Table(name = "students")
class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long   id;

    @Column(nullable = false) private String name;
    @Column                   private String department;
    @Column                   private int    age;
    @Column(unique = true)    private String email;

    public StudentEntity() {}

    public StudentEntity(String name, String department, int age, String email) {
        this.name = name; this.department = department;
        this.age  = age;  this.email = email;
    }

    // Getters & Setters
    public Long   getId()               { return id; }
    public String getName()             { return name; }
    public void   setName(String v)     { this.name = v; }
    public String getDepartment()       { return department; }
    public void   setDepartment(String v){ this.department = v; }
    public int    getAge()              { return age; }
    public void   setAge(int v)         { this.age = v; }
    public String getEmail()            { return email; }
    public void   setEmail(String v)    { this.email = v; }
}

// ── Repository — JpaRepository gives all CRUD methods for free ───────────────
interface StudentCrudRepository extends JpaRepository<StudentEntity, Long> {}

// ── Service ───────────────────────────────────────────────────────────────────
@Service
class StudentCrudService {

    @Autowired
    private StudentCrudRepository repo;

    // CREATE
    public StudentEntity create(StudentEntity s)       { return repo.save(s); }

    // READ ALL
    public List<StudentEntity> getAll()                { return repo.findAll(); }

    // READ BY ID
    public Optional<StudentEntity> getById(Long id)    { return repo.findById(id); }

    // UPDATE
    public StudentEntity update(Long id, StudentEntity updated) {
        return repo.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setDepartment(updated.getDepartment());
            existing.setAge(updated.getAge());
            existing.setEmail(updated.getEmail());
            return repo.save(existing);
        }).orElseThrow(() -> new RuntimeException("Student not found: " + id));
    }

    // DELETE
    public String delete(Long id) {
        if (repo.existsById(id)) { repo.deleteById(id); return "✅ Deleted student ID " + id; }
        return "❌ Student ID " + id + " not found.";
    }
}

// ── REST Controller — Full CRUD endpoints ────────────────────────────────────
@RestController
@RequestMapping("/students")
class StudentCrudController {

    @Autowired
    private StudentCrudService service;

    // ── CREATE: POST /students ────────────────────────────────────────────────
    // Postman: POST http://localhost:8085/students
    // Body (JSON): {"name":"Sam","department":"IT","age":21,"email":"sam@mail.com"}
    @PostMapping
    public ResponseEntity<StudentEntity> create(@RequestBody StudentEntity student) {
        return ResponseEntity.ok(service.create(student));
    }

    // ── READ ALL: GET /students ───────────────────────────────────────────────
    // Postman: GET http://localhost:8085/students
    @GetMapping
    public ResponseEntity<List<StudentEntity>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // ── READ BY ID: GET /students/{id} ───────────────────────────────────────
    // Postman: GET http://localhost:8085/students/1
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return service.getById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── UPDATE: PUT /students/{id} ───────────────────────────────────────────
    // Postman: PUT http://localhost:8085/students/1
    // Body (JSON): {"name":"Ravi K","department":"CSE","age":21,"email":"ravi@mail.com"}
    @PutMapping("/{id}")
    public ResponseEntity<StudentEntity> update(@PathVariable Long id,
                                                @RequestBody StudentEntity student) {
        return ResponseEntity.ok(service.update(id, student));
    }

    // ── DELETE: DELETE /students/{id} ────────────────────────────────────────
    // Postman: DELETE http://localhost:8085/students/1
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }
}

// ═══════════════════════════════════════════════════════════════════
//  POSTMAN TEST GUIDE
// ═══════════════════════════════════════════════════════════════════
//
//  1. CREATE (POST)
//     URL    : POST http://localhost:8085/students
//     Headers: Content-Type: application/json
//     Body   : {"name":"Sam","department":"IT","age":21,"email":"sam@mail.com"}
//
//  2. READ ALL (GET)
//     URL    : GET http://localhost:8085/students
//
//  3. READ ONE (GET)
//     URL    : GET http://localhost:8085/students/1
//
//  4. UPDATE (PUT)
//     URL    : PUT http://localhost:8085/students/1
//     Headers: Content-Type: application/json
//     Body   : {"name":"Sam Updated","department":"CSE","age":22,"email":"sam@mail.com"}
//
//  5. DELETE (DELETE)
//     URL    : DELETE http://localhost:8085/students/1
// ═══════════════════════════════════════════════════════════════════
