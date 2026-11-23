package pt.studymatch.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pt.studymatch.backend.dto.StudentProfileResponse;
import pt.studymatch.backend.service.AccessControlService;
import pt.studymatch.backend.service.StudentProfileService;

@RestController
@RequestMapping("/api/students")
public class StudentProfileController {

    private final StudentProfileService studentProfileService;
    private final AccessControlService accessControl;

    public StudentProfileController(
            StudentProfileService studentProfileService,
            AccessControlService accessControl) {
        this.studentProfileService = studentProfileService;
        this.accessControl = accessControl;
    }

    @GetMapping("/{studentNumber}/profile")
    public StudentProfileResponse getStudentProfile(
            @PathVariable String studentNumber) {

        accessControl.checkStudentAccess(studentNumber);
        return studentProfileService.getStudentProfile(studentNumber);
    }
}
