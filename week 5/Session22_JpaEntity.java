// ═══════════════════════════════════════════════════════════════════
//  WEEK 5 — SESSION 22
//  Task: ORM Mapping using @Entity, @Id, @Table, @Column on Student
//  File: Session22_JpaEntity.java
//
//  pom.xml dependencies:
//    - spring-boot-starter-data-jpa
//    - spring-boot-starter-web
//    - mysql-connector-java  (or h2)
//
//  application.properties:
//    spring.jpa.hibernate.ddl-auto=update
//    spring.jpa.show-sql=true
//
//  Test: GET http://localhost:8085/student/schema
// ═══════════════════════════════════════════════════════════════════

package com.week5.session22;

import jakarta.persistence.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// ── Entry Point ───────────────────────────────────────────────────────────────
@SpringBootApplication
public class Session22_JpaEntity {
    public static void main(String[] args) {
        SpringApplication.run(Session22_JpaEntity.class, args);
        System.out.println("✅ Session 22 — JPA Entity mapping started.");
        System.out.println("   Hibernate will auto-create 'students' table from @Entity.");
    }
}

// ── Student Entity — ORM Mapping ──────────────────────────────────────────────
// @Entity   → marks this class as a JPA entity (mapped to a DB table)
// @Table    → specifies exact table name in the database
// @Id       → marks the primary key field
// @Column   → maps field to a specific column name with constraints
@Entity
@Table(name = "students")          // maps to DB table named "students"
class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // auto-increment PK
    @Column(name = "student_id")   // DB column: student_id
    private Long id;

    @Column(name = "student_name", nullable = false, length = 100)
    private String name;

    @Column(name = "department", length = 50)
    private String department;

    @Column(name = "age")
    private int age;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    // Default constructor — required by JPA
    public Student() {}

    public Student(String name, String department, int age, String email) {
        this.name       = name;
        this.department = department;
        this.age        = age;
        this.email      = email;
    }

    // Getters & Setters
    public Long   getId()            { return id; }
    public String getName()          { return name; }
    public void   setName(String n)  { this.name = n; }
    public String getDepartment()    { return department; }
    public void   setDepartment(String d) { this.department = d; }
    public int    getAge()           { return age; }
    public void   setAge(int a)      { this.age = a; }
    public String getEmail()         { return email; }
    public void   setEmail(String e) { this.email = e; }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', dept='" +
               department + "', age=" + age + ", email='" + email + "'}";
    }
}

// ── Controller to confirm entity mapping info ─────────────────────────────────
@RestController
@RequestMapping("/student")
class StudentSchemaController {

    // GET /student/schema — shows ORM mapping details
    @GetMapping("/schema")
    public String schema() {
        return "ORM Mapping Active:\n" +
               "Class     : Student\n" +
               "Table     : students\n" +
               "PK Column : student_id  (@Id + @GeneratedValue)\n" +
               "Columns   : student_name, department, age, email\n" +
               "Hibernate : will auto-create table on startup (ddl-auto=update)";
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  application.properties additions for Session 22:
// ─────────────────────────────────────────────────────────────────────────────
//  spring.jpa.hibernate.ddl-auto=update
//  spring.jpa.show-sql=true
//  spring.jpa.properties.hibernate.format_sql=true
// ─────────────────────────────────────────────────────────────────────────────
