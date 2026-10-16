
package pt.studymatch.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pt.studymatch.backend.dto.StudentProfileResponse;
import pt.studymatch.backend.service.StudentProfileService;

@RestController
@RequestMapping("/api/students")
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    public StudentProfileController(StudentProfileService studentProfileService) {
        this.studentProfileService = studentProfileService;
    }

    @GetMapping("/{studentNumber}/profile")
    public StudentProfileResponse getStudentProfile(
            @PathVariable String studentNumber) {

        return studentProfileService.getStudentProfile(studentNumber);
    }
}
