package pt.studymatch.backend.controller;

import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pt.studymatch.backend.dto.CreateStudentRequest;
import pt.studymatch.backend.dto.StudentResponse;
import pt.studymatch.backend.service.StudentService;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService service;

    public StudentController(StudentService service) { this.service = service; }

    @GetMapping
    public List<StudentResponse> list() { return service.list(); }

    @GetMapping("/{studentNumber}")
    public StudentResponse find(@PathVariable String studentNumber) {
        return service.find(studentNumber);
    }

    @PostMapping
    public ResponseEntity<StudentResponse> create(@Valid @RequestBody CreateStudentRequest request) {
        StudentResponse student = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{studentNumber}").buildAndExpand(student.studentNumber()).toUri();
        return ResponseEntity.created(location).body(student);
    }
}
