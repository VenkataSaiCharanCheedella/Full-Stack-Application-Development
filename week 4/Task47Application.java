// ═══════════════════════════════════════════════════════════════════
//  TASK 4.7 — Spring MVC Application (Annotation-based, No XML)
//             Syllabus Task 9
//             Controller → Model → View (MVC flow)
//             Displays Employee Details
//  File: Task47Application.java
//
//  DEPENDENCIES NEEDED IN pom.xml:
//    - spring-boot-starter-web
//    - spring-boot-starter-thymeleaf   (for HTML view rendering)
//    - spring-boot-devtools
//
//  HOW TO RUN:
//  1. Run Task47Application.main()
//  2. Test REST endpoints (JSON):
//       GET  http://localhost:8085/mvc/employees
//       GET  http://localhost:8085/mvc/employees/1
//       GET  http://localhost:8085/mvc/employees/search?name=Ravi
//       POST http://localhost:8085/mvc/employees  (JSON body)
//
//  3. Test HTML view (MVC flow with Model + View):
//       GET  http://localhost:8085/view/employees
//       GET  http://localhost:8085/view/employees/1
//
//  NOTE: HTML views require Thymeleaf templates in src/main/resources/templates/
//        Template files are shown as comments at the bottom of this file.
// ═══════════════════════════════════════════════════════════════════

package com.week4.task47;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

// ── Entry point ───────────────────────────────────────────────────────────────
@SpringBootApplication
public class Task47Application {
    public static void main(String[] args) {
        SpringApplication.run(Task47Application.class, args);
        System.out.println("\n" + "═".repeat(55));
        System.out.println("  Task 4.7 — Spring MVC (Annotation-Based, No XML)");
        System.out.println("═".repeat(55));
        System.out.println("  REST : GET http://localhost:8085/mvc/employees");
        System.out.println("  HTML : GET http://localhost:8085/view/employees");
        System.out.println("═".repeat(55) + "\n");
    }
}

// ── Employee Model ────────────────────────────────────────────────────────────
// The "M" in MVC — carries data between controller and view
class EmployeeModel {
    private int    id;
    private String name;
    private String department;
    private String email;
    private double salary;

    // Default constructor (needed for @RequestBody JSON deserialization)
    public EmployeeModel() {}

    public EmployeeModel(int id, String name, String department,
                         String email, double salary) {
        this.id         = id;
        this.name       = name;
        this.department = department;
        this.email      = email;
        this.salary     = salary;
    }

    // Getters & Setters
    public int    getId()            { return id; }
    public void   setId(int id)      { this.id = id; }
    public String getName()          { return name; }
    public void   setName(String n)  { this.name = n; }
    public String getDepartment()    { return department; }
    public void   setDepartment(String d) { this.department = d; }
    public String getEmail()         { return email; }
    public void   setEmail(String e) { this.email = e; }
    public double getSalary()        { return salary; }
    public void   setSalary(double s){ this.salary = s; }

    @Override
    public String toString() {
        return "Employee{id=" + id + ", name='" + name +
               "', dept='" + department + "', email='" + email +
               "', salary=" + salary + "}";
    }
}

// ── Employee Repository — In-Memory Store ─────────────────────────────────────
@Component
class EmployeeMvcRepository {

    private final Map<Integer, EmployeeModel> store = new LinkedHashMap<>();

    public EmployeeMvcRepository() {
        // Pre-load sample data
        store.put(1, new EmployeeModel(1, "Ravi Kumar",   "IT",      "ravi@company.com",   72000));
        store.put(2, new EmployeeModel(2, "Priya Sharma", "Finance", "priya@company.com",  65000));
        store.put(3, new EmployeeModel(3, "Arjun Singh",  "IT",      "arjun@company.com",  80000));
        store.put(4, new EmployeeModel(4, "Meena Patel",  "HR",      "meena@company.com",  58000));
        store.put(5, new EmployeeModel(5, "Kiran Das",    "Marketing","kiran@company.com", 61000));
    }

    public List<EmployeeModel> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<EmployeeModel> findById(int id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<EmployeeModel> findByName(String name) {
        return store.values().stream()
                .filter(e -> e.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<EmployeeModel> findByDept(String dept) {
        return store.values().stream()
                .filter(e -> e.getDepartment().equalsIgnoreCase(dept))
                .collect(Collectors.toList());
    }

    public EmployeeModel save(EmployeeModel emp) {
        store.put(emp.getId(), emp);
        return emp;
    }

    public boolean delete(int id) {
        return store.remove(id) != null;
    }
}

// ── Service Layer ─────────────────────────────────────────────────────────────
@Service
class EmployeeMvcService {

    @Autowired
    private EmployeeMvcRepository repository;

    public List<EmployeeModel> getAll()                    { return repository.findAll(); }
    public Optional<EmployeeModel> getById(int id)         { return repository.findById(id); }
    public List<EmployeeModel> searchByName(String name)   { return repository.findByName(name); }
    public List<EmployeeModel> getByDept(String dept)      { return repository.findByDept(dept); }
    public EmployeeModel create(EmployeeModel emp)          { return repository.save(emp); }
    public boolean delete(int id)                          { return repository.delete(id); }
}

// ════════════════════════════════════════════════════════════════════
//  REST CONTROLLER — The "C" in MVC (returns JSON)
//  @RestController = @Controller + @ResponseBody
//  No XML configuration — fully annotation-driven.
// ════════════════════════════════════════════════════════════════════
@RestController
@RequestMapping("/mvc/employees")
class EmployeeRestController {

    // ★ Spring MVC DI — service injected by Spring (no new keyword)
    @Autowired
    private EmployeeMvcService service;

    // GET /mvc/employees — returns all employees as JSON
    @GetMapping
    public ResponseEntity<List<EmployeeModel>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // GET /mvc/employees/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable int id) {
        return service.getById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /mvc/employees/search?name=Ravi
    @GetMapping("/search")
    public ResponseEntity<List<EmployeeModel>> search(@RequestParam String name) {
        List<EmployeeModel> results = service.searchByName(name);
        if (results.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(results);
    }

    // GET /mvc/employees/dept/IT
    @GetMapping("/dept/{dept}")
    public ResponseEntity<List<EmployeeModel>> byDept(@PathVariable String dept) {
        return ResponseEntity.ok(service.getByDept(dept));
    }

    // POST /mvc/employees — create new employee
    // Body: {"id":6,"name":"Sam","department":"IT","email":"sam@co.com","salary":75000}
    @PostMapping
    public ResponseEntity<EmployeeModel> create(@RequestBody EmployeeModel emp) {
        return ResponseEntity.ok(service.create(emp));
    }

    // DELETE /mvc/employees/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {
        if (service.delete(id)) {
            return ResponseEntity.ok("✅ Employee " + id + " deleted.");
        }
        return ResponseEntity.notFound().build();
    }
}

// ════════════════════════════════════════════════════════════════════
//  MVC VIEW CONTROLLER — The full MVC flow (returns HTML via Thymeleaf)
//  @Controller (NOT @RestController) — returns VIEW NAME, not JSON
//  Model object carries data to the template (Thymeleaf renders it)
// ════════════════════════════════════════════════════════════════════
@org.springframework.stereotype.Controller
@RequestMapping("/view")
class EmployeeViewController {

    @Autowired
    private EmployeeMvcService service;

    // ★ MVC FLOW:
    //   1. GET /view/employees arrives
    //   2. Controller calls service — populates Model
    //   3. Returns view name "employees" → Thymeleaf renders employees.html
    //   4. HTML response sent to browser
    @GetMapping("/employees")
    public String listEmployees(Model model) {
        // Model carries data to the view template
        model.addAttribute("employees", service.getAll());
        model.addAttribute("title",     "Employee List — Spring MVC");
        model.addAttribute("total",     service.getAll().size());
        return "employees";   // → templates/employees.html
    }

    // GET /view/employees/{id} → templates/employee-detail.html
    @GetMapping("/employees/{id}")
    public String employeeDetail(@PathVariable int id, Model model) {
        Optional<EmployeeModel> emp = service.getById(id);
        if (emp.isPresent()) {
            model.addAttribute("employee", emp.get());
            model.addAttribute("title",    "Employee Detail — " + emp.get().getName());
            return "employee-detail";   // → templates/employee-detail.html
        }
        model.addAttribute("error", "Employee with ID " + id + " not found.");
        return "error";                 // → templates/error.html
    }
}

// ════════════════════════════════════════════════════════════════════
//  THYMELEAF TEMPLATES
//  Create these files in: src/main/resources/templates/
// ════════════════════════════════════════════════════════════════════

// ─── templates/employees.html ─────────────────────────────────────────────────
/*
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title th:text="${title}">Employees</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 30px; background:#f4f6f9; }
        h1   { color: #2c3e50; }
        table{ width:100%; border-collapse:collapse; background:#fff; }
        th   { background:#2c3e50; color:#fff; padding:10px; text-align:left; }
        td   { padding:9px 10px; border-bottom:1px solid #ddd; }
        tr:hover { background:#f0f4ff; }
        .badge { background:#2980b9; color:#fff; padding:2px 8px; border-radius:12px; font-size:12px; }
        a    { color:#2980b9; text-decoration:none; }
    </style>
</head>
<body>
    <h1 th:text="${title}">Employee List</h1>
    <p>Total Employees: <strong th:text="${total}">0</strong></p>

    <table>
        <thead>
            <tr>
                <th>ID</th><th>Name</th><th>Department</th>
                <th>Email</th><th>Salary</th><th>Detail</th>
            </tr>
        </thead>
        <tbody>
            <tr th:each="emp : ${employees}">
                <td th:text="${emp.id}">1</td>
                <td th:text="${emp.name}">Name</td>
                <td><span class="badge" th:text="${emp.department}">Dept</span></td>
                <td th:text="${emp.email}">email</td>
                <td th:text="'₹' + ${emp.salary}">0</td>
                <td><a th:href="@{/view/employees/{id}(id=${emp.id})}">View →</a></td>
            </tr>
        </tbody>
    </table>
</body>
</html>
*/

// ─── templates/employee-detail.html ──────────────────────────────────────────
/*
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title th:text="${title}">Employee Detail</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 30px; background:#f4f6f9; }
        .card { background:#fff; padding:24px; border-radius:8px; max-width:420px;
                box-shadow:0 2px 8px rgba(0,0,0,0.1); }
        h2   { color:#2c3e50; }
        .row { margin:10px 0; font-size:15px; }
        .lbl { color:#7f8c8d; font-size:12px; text-transform:uppercase; }
        a    { display:inline-block; margin-top:16px; color:#2980b9; }
    </style>
</head>
<body>
    <div class="card">
        <h2 th:text="${employee.name}">Name</h2>
        <div class="row"><span class="lbl">ID</span><br/><b th:text="${employee.id}">1</b></div>
        <div class="row"><span class="lbl">Department</span><br/><b th:text="${employee.department}">IT</b></div>
        <div class="row"><span class="lbl">Email</span><br/><b th:text="${employee.email}">email</b></div>
        <div class="row"><span class="lbl">Salary</span><br/><b th:text="'₹' + ${employee.salary}">0</b></div>
        <a href="/view/employees">← Back to List</a>
    </div>
</body>
</html>
*/

// ─── templates/error.html ─────────────────────────────────────────────────────
/*
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head><title>Error</title></head>
<body>
    <h2 style="color:red" th:text="${error}">Error</h2>
    <a href="/view/employees">← Back to List</a>
</body>
</html>
*/
