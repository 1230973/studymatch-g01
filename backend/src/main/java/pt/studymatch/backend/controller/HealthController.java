package pt.studymatch.backend.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.studymatch.backend.repository.StudentRepository;

@RestController
@RequestMapping("/api/health")
public class HealthController {
    private final StudentRepository students;

    public HealthController(StudentRepository students) { this.students = students; }

    @GetMapping
    public Map<String, String> status() {
        students.count(); // Confirma acesso real a persistencia
        return Map.of("status", "UP", "database", "UP", "service", "studymatch-backend");
    }
}
