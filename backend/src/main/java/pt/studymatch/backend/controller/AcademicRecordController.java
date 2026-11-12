package pt.studymatch.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pt.studymatch.backend.dto.AcademicRecordResponse;
import pt.studymatch.backend.dto.CreateAcademicRecordRequest;
import pt.studymatch.backend.service.AccessControlService;
import pt.studymatch.backend.service.AcademicRecordService;

@RestController
@RequestMapping("/api/students/{studentNumber}/academic-records")
public class AcademicRecordController {

    private final AcademicRecordService academicRecordService;
    private final AccessControlService accessControl;

    public AcademicRecordController(
            AcademicRecordService academicRecordService,
            AccessControlService accessControl) {
        this.academicRecordService = academicRecordService;
        this.accessControl = accessControl;
    }

    // Consultar o historico academico
    @GetMapping
    public List<AcademicRecordResponse> getHistory(
            @PathVariable String studentNumber) {

        accessControl.checkStudentAccess(studentNumber);
        return academicRecordService.getHistory(studentNumber);
    }

    // Registar uma nova classificacao
    @PostMapping
    public ResponseEntity<AcademicRecordResponse> create(
            @PathVariable String studentNumber,
            @RequestBody CreateAcademicRecordRequest request) {

        AcademicRecordResponse response =
                academicRecordService.create(studentNumber, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
