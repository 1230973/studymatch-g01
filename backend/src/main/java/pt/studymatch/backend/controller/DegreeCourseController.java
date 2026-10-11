package pt.studymatch.backend.controller;

import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pt.studymatch.backend.dto.CreateDegreeCourseRequest;
import pt.studymatch.backend.dto.DegreeCourseResponse;
import pt.studymatch.backend.service.DegreeCourseService;

@RestController
@RequestMapping("/api/degree-courses")
public class DegreeCourseController {
    private final DegreeCourseService service;

    public DegreeCourseController(DegreeCourseService service) { this.service = service; }

    @GetMapping
    public List<DegreeCourseResponse> list() { return service.list(); }

    @PostMapping
    public ResponseEntity<DegreeCourseResponse> create(@Valid @RequestBody CreateDegreeCourseRequest request) {
        DegreeCourseResponse course = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{code}").buildAndExpand(course.code()).toUri();
        return ResponseEntity.created(location).body(course);
    }
}
