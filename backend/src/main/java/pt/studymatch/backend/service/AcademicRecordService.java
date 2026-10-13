
package pt.studymatch.backend.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import pt.studymatch.backend.dto.AcademicRecordResponse;
import pt.studymatch.backend.dto.CreateAcademicRecordRequest;
import pt.studymatch.backend.exception.ResourceNotFoundException;
import pt.studymatch.backend.model.AcademicRecord;
import pt.studymatch.backend.model.CourseUnit;
import pt.studymatch.backend.model.Student;
import pt.studymatch.backend.repository.AcademicRecordRepository;
import pt.studymatch.backend.repository.CourseUnitRepository;
import pt.studymatch.backend.repository.StudentRepository;

@Service
public class AcademicRecordService {

    private final AcademicRecordRepository academicRecords;
    private final StudentRepository students;
    private final CourseUnitRepository courseUnits;

    public AcademicRecordService(
            AcademicRecordRepository academicRecords,
            StudentRepository students,
            CourseUnitRepository courseUnits) {

        this.academicRecords = academicRecords;
        this.students = students;
        this.courseUnits = courseUnits;
    }

    // Consultar o histórico académico de um estudante
    @Transactional(readOnly = true)
    public List<AcademicRecordResponse> getHistory(String studentNumber) {

        Student student = students.findByStudentNumberIgnoreCase(studentNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Estudante nao encontrado."));

        return academicRecords.findByStudentOrderByIdAsc(student)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Registar uma nova classificação
    @Transactional
    public AcademicRecordResponse create(
            String studentNumber,
            CreateAcademicRecordRequest request) {

        if (request == null
                || request.courseUnitCode() == null
                || request.courseUnitCode().isBlank()
                || request.grade() == null
                || request.academicYear() == null
                || request.academicYear().isBlank()
                || request.semester() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Preenche todos os campos.");
        }

        if (request.grade().compareTo(BigDecimal.ZERO) < 0
                || request.grade().compareTo(new BigDecimal("20")) > 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "A nota deve estar entre 0 e 20.");
        }

        if (request.semester() < 1 || request.semester() > 2) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "O semestre deve ser 1 ou 2.");
        }

        String academicYear = request.academicYear().trim();

        if (!academicYear.matches("\\d{4}/\\d{4}")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O ano letivo deve ter o formato 2025/2026.");
        }

        Student student = students.findByStudentNumberIgnoreCase(studentNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Estudante nao encontrado."));

        String code = request.courseUnitCode()
                .trim()
                .toUpperCase(Locale.ROOT);

        CourseUnit courseUnit = courseUnits.findByCode(code)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Unidade curricular nao encontrada."));

        AcademicRecord academicRecord = new AcademicRecord(
                student,
                courseUnit,
                request.grade(),
                academicYear,
                request.semester()
        );

        return toResponse(academicRecords.save(academicRecord));
    }

    // Converter a entidade num DTO de resposta
    private AcademicRecordResponse toResponse(AcademicRecord academicRecord) {

        return new AcademicRecordResponse(
                academicRecord.getId(),
                academicRecord.getCourseUnit().getCode(),
                academicRecord.getCourseUnit().getName(),
                academicRecord.getGrade(),
                academicRecord.getAcademicYear(),
                academicRecord.getSemester()
        );
    }
}
