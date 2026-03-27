// ═══════════════════════════════════════════════════════════════════
//  WEEK 5 — SESSION 24 & 25
//  Task: Data Access Layer using Spring Data JPA
//        JpaRepository + Custom Query Methods
//        Retrieve students by department, age, name conditions
//  File: Session24_25_DataAccessLayer.java
//
//  pom.xml dependencies:
//    - spring-boot-starter-data-jpa
//    - spring-boot-starter-web
//    - mysql-connector-java  (or h2)
//
//  Test endpoints listed at bottom of file
// ═══════════════════════════════════════════════════════════════════

package com.week5.session24_25;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// ── Entry Point ───────────────────────────────────────────────────────────────
@SpringBootApplication
public class Session24_25_DataAccessLayer implements CommandLineRunner {

    @Autowired
    private StudentDataService service;

    public static void main(String[] args) {
        SpringApplication.run(Session24_25_DataAccessLayer.class, args);
    }

    @Override
    public void run(String... args) {
        // Pre-load sample data for testing all query methods
        service.save(new StudentData("Ravi Kumar",   "CSE", 20, "ravi@mail.com"));
        service.save(new StudentData("Priya Sharma", "ECE", 22, "priya@mail.com"));
        service.save(new StudentData("Arjun Singh",  "CSE", 21, "arjun@mail.com"));
        service.save(new StudentData("Meena Patel",  "IT",  23, "meena@mail.com"));
        service.save(new StudentData("Kiran Das",    "CSE", 20, "kiran@mail.com"));
        service.save(new StudentData("Sunita Roy",   "ECE", 24, "sunita@mail.com"));
        System.out.println("✅ Session 24/25 — Data Access Layer ready.");
        System.out.println("   Test: GET http://localhost:8085/data/students");
    }
}

// ── Entity ────────────────────────────────────────────────────────────────────
@Entity
@Table(name = "students")
class StudentData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long   id;

    @Column(nullable = false) private String name;
    @Column                   private String department;
    @Column                   private int    age;
    @Column(unique = true)    private String email;

    public StudentData() {}

    public StudentData(String name, String department, int age, String email) {
        this.name = name; this.department = department;
        this.age  = age;  this.email = email;
    }

    // Getters & Setters
    public Long   getId()               { return id; }
    public String getName()             { return name; }
    public void   setName(String v)     { name = v; }
    public String getDepartment()       { return department; }
    public void   setDepartment(String v){ department = v; }
    public int    getAge()              { return age; }
    public void   setAge(int v)         { age = v; }
    public String getEmail()            { return email; }
    public void   setEmail(String v)    { email = v; }
}

// ════════════════════════════════════════════════════════════════════
//  REPOSITORY — Spring Data JPA (Session 24 & 25 core)
//
//  JpaRepository<StudentData, Long> provides out-of-the-box:
//    save(), findAll(), findById(), deleteById(), count(), etc.
//
//  Custom query methods below — Spring generates SQL automatically
//  from the METHOD NAME (Query Derivation).
//  @Query methods use JPQL (Java Persistence Query Language).
// ════════════════════════════════════════════════════════════════════
interface StudentDataRepository extends JpaRepository<StudentData, Long> {

    // ── DERIVED QUERY METHODS (Spring generates SQL from method name) ─────────

    // SELECT * FROM students WHERE department = ?
    List<StudentData> findByDepartment(String department);

    // SELECT * FROM students WHERE age = ?
    List<StudentData> findByAge(int age);

    // SELECT * FROM students WHERE age > ?
    List<StudentData> findByAgeGreaterThan(int age);

    // SELECT * FROM students WHERE age < ?
    List<StudentData> findByAgeLessThan(int age);

    // SELECT * FROM students WHERE age BETWEEN ? AND ?
    List<StudentData> findByAgeBetween(int minAge, int maxAge);

    // SELECT * FROM students WHERE department = ? AND age = ?
    List<StudentData> findByDepartmentAndAge(String department, int age);

    // SELECT * FROM students WHERE name LIKE '%keyword%'
    List<StudentData> findByNameContainingIgnoreCase(String keyword);

    // ── CUSTOM @Query METHODS (JPQL) ──────────────────────────────────────────

    // JPQL query — get all students in a department, ordered by name
    @Query("SELECT s FROM StudentData s WHERE s.department = :dept ORDER BY s.name")
    List<StudentData> findByDeptOrdered(@Param("dept") String dept);

    // JPQL query — get students older than given age in a specific department
    @Query("SELECT s FROM StudentData s WHERE s.department = :dept AND s.age > :age")
    List<StudentData> findByDeptAndAgeGreaterThan(@Param("dept") String dept,
                                                   @Param("age")  int age);

    // Native SQL query — count students per department
    @Query(value = "SELECT department, COUNT(*) as total FROM students GROUP BY department",
           nativeQuery = true)
    List<Object[]> countByDepartment();

    // JPQL — find youngest student in a department
    @Query("SELECT s FROM StudentData s WHERE s.department = :dept ORDER BY s.age ASC")
    List<StudentData> findYoungestInDept(@Param("dept") String dept);
}

// ── Service Layer ─────────────────────────────────────────────────────────────
@Service
class StudentDataService {

    @Autowired
    private StudentDataRepository repo;

    public StudentData              save(StudentData s)                    { return repo.save(s); }
    public List<StudentData>        getAll()                               { return repo.findAll(); }
    public Optional<StudentData>    getById(Long id)                       { return repo.findById(id); }
    public List<StudentData>        getByDept(String dept)                 { return repo.findByDepartment(dept); }
    public List<StudentData>        getByAge(int age)                      { return repo.findByAge(age); }
    public List<StudentData>        getByAgeGreaterThan(int age)           { return repo.findByAgeGreaterThan(age); }
    public List<StudentData>        getByAgeBetween(int min, int max)      { return repo.findByAgeBetween(min, max); }
    public List<StudentData>        getByDeptAndAge(String dept, int age)  { return repo.findByDepartmentAndAge(dept, age); }
    public List<StudentData>        searchByName(String kw)                { return repo.findByNameContainingIgnoreCase(kw); }
    public List<StudentData>        getByDeptOrdered(String dept)          { return repo.findByDeptOrdered(dept); }
    public List<StudentData>        getByDeptAgeGT(String dept, int age)   { return repo.findByDeptAndAgeGreaterThan(dept, age); }
    public List<Object[]>           getDeptCount()                         { return repo.countByDepartment(); }
    public List<StudentData>        getYoungestInDept(String dept)         { return repo.findYoungestInDept(dept); }
    public long                     totalCount()                           { return repo.count(); }
    public void                     deleteById(Long id)                    { repo.deleteById(id); }
}

// ── REST Controller — Data Access Layer endpoints ────────────────────────────
@RestController
@RequestMapping("/data/students")
class StudentDataController {

    @Autowired
    private StudentDataService service;

    // GET /data/students                        → all students
    @GetMapping
    public ResponseEntity<List<StudentData>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // GET /data/students/{id}                   → by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return service.getById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /data/students/dept/CSE               → by department
    @GetMapping("/dept/{dept}")
    public ResponseEntity<List<StudentData>> getByDept(@PathVariable String dept) {
        return ResponseEntity.ok(service.getByDept(dept));
    }

    // GET /data/students/age/20                 → exact age match
    @GetMapping("/age/{age}")
    public ResponseEntity<List<StudentData>> getByAge(@PathVariable int age) {
        return ResponseEntity.ok(service.getByAge(age));
    }

    // GET /data/students/age/above/21           → age greater than
    @GetMapping("/age/above/{age}")
    public ResponseEntity<List<StudentData>> getAgeAbove(@PathVariable int age) {
        return ResponseEntity.ok(service.getByAgeGreaterThan(age));
    }

    // GET /data/students/age/range/20/22        → age between min and max
    @GetMapping("/age/range/{min}/{max}")
    public ResponseEntity<List<StudentData>> getAgeBetween(@PathVariable int min,
                                                            @PathVariable int max) {
        return ResponseEntity.ok(service.getByAgeBetween(min, max));
    }

    // GET /data/students/filter?dept=CSE&age=20 → department AND age
    @GetMapping("/filter")
    public ResponseEntity<List<StudentData>> getByDeptAndAge(@RequestParam String dept,
                                                              @RequestParam int age) {
        return ResponseEntity.ok(service.getByDeptAndAge(dept, age));
    }

    // GET /data/students/search?name=ravi       → name contains keyword
    @GetMapping("/search")
    public ResponseEntity<List<StudentData>> search(@RequestParam String name) {
        return ResponseEntity.ok(service.searchByName(name));
    }

    // GET /data/students/dept/CSE/sorted        → custom @Query ordered by name
    @GetMapping("/dept/{dept}/sorted")
    public ResponseEntity<List<StudentData>> getSortedByDept(@PathVariable String dept) {
        return ResponseEntity.ok(service.getByDeptOrdered(dept));
    }

    // GET /data/students/dept/CSE/age/above/20  → custom @Query dept + age filter
    @GetMapping("/dept/{dept}/age/above/{age}")
    public ResponseEntity<List<StudentData>> getDeptAgeGT(@PathVariable String dept,
                                                           @PathVariable int age) {
        return ResponseEntity.ok(service.getByDeptAgeGT(dept, age));
    }

    // GET /data/students/count/dept             → native SQL count per department
    @GetMapping("/count/dept")
    public ResponseEntity<List<Object[]>> countByDept() {
        return ResponseEntity.ok(service.getDeptCount());
    }

    // GET /data/students/dept/CSE/youngest      → youngest in department
    @GetMapping("/dept/{dept}/youngest")
    public ResponseEntity<List<StudentData>> youngest(@PathVariable String dept) {
        return ResponseEntity.ok(service.getYoungestInDept(dept));
    }

    // GET /data/students/count                  → total record count
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(service.totalCount());
    }

    // POST /data/students                       → create
    @PostMapping
    public ResponseEntity<StudentData> create(@RequestBody StudentData s) {
        return ResponseEntity.ok(service.save(s));
    }

    // DELETE /data/students/{id}                → delete by id
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.ok("✅ Deleted student ID: " + id);
    }
}

// ═══════════════════════════════════════════════════════════════════
//  ALL TEST ENDPOINTS (Postman / Browser)
// ═══════════════════════════════════════════════════════════════════
//
//  GET  /data/students                     → all
//  GET  /data/students/1                   → by ID
//  GET  /data/students/dept/CSE            → by department
//  GET  /data/students/age/20              → exact age
//  GET  /data/students/age/above/21        → age > 21
//  GET  /data/students/age/range/20/22     → age between 20-22
//  GET  /data/students/filter?dept=CSE&age=20  → dept AND age
//  GET  /data/students/search?name=ravi    → name contains
//  GET  /data/students/dept/CSE/sorted     → @Query sorted
//  GET  /data/students/dept/CSE/age/above/20   → @Query dept+age
//  GET  /data/students/count/dept          → native SQL count
//  GET  /data/students/dept/CSE/youngest   → youngest in dept
//  GET  /data/students/count               → total count
//  POST /data/students                     → create new
//  DELETE /data/students/1                 → delete by id
// ═══════════════════════════════════════════════════════════════════
