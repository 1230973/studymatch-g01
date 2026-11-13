package pt.studymatch.backend.service;

import java.util.List;
import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.studymatch.backend.dto.CreateStudentRequest;
import pt.studymatch.backend.dto.StudentResponse;
import pt.studymatch.backend.dto.UpdateStudentRequest;
import pt.studymatch.backend.exception.DuplicateResourceException;
import pt.studymatch.backend.exception.ResourceNotFoundException;
import pt.studymatch.backend.model.DegreeCourse;
import pt.studymatch.backend.model.Student;
import pt.studymatch.backend.model.UserAccount;
import pt.studymatch.backend.repository.DegreeCourseRepository;
import pt.studymatch.backend.repository.StudentRepository;
import pt.studymatch.backend.repository.UserAccountRepository;

@Service
public class StudentService {
    private final StudentRepository students;
    private final DegreeCourseRepository courses;
    private final UserAccountRepository userAccounts;
    private final PasswordEncoder passwordEncoder;

    public StudentService(
            StudentRepository students,
            DegreeCourseRepository courses,
            UserAccountRepository userAccounts,
            PasswordEncoder passwordEncoder) {
        this.students = students;
        this.courses = courses;
        this.userAccounts = userAccounts;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> list() {
        return students.findAllByOrderByStudentNumberAsc().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public StudentResponse find(String studentNumber) {
        Student student = students.findByStudentNumberIgnoreCase(studentNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Estudante nao encontrado."));
        return toResponse(student);
    }

    @Transactional
    public StudentResponse create(CreateStudentRequest request) {
        String number = request.studentNumber().trim().toUpperCase(Locale.ROOT);
        String email = request.institutionalEmail().trim().toLowerCase(Locale.ROOT);
        String courseCode = request.degreeCourseCode().trim();

        if (students.existsByStudentNumberIgnoreCase(number)) {
            throw new DuplicateResourceException("Ja existe um estudante com esse numero.");
        }
        if (students.existsByInstitutionalEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Ja existe um estudante com esse email.");
        }

        DegreeCourse course = courses.findByCodeIgnoreCase(courseCode)
                .orElseThrow(() -> new ResourceNotFoundException("Curso nao encontrado. Regista primeiro o curso."));

        Student student = students.save(new Student(number, request.name().trim(), email,
                request.enrolledYear(), course));

        if (!userAccounts.existsByUsernameIgnoreCase(number)) {
            userAccounts.save(new UserAccount(
                    number,
                    passwordEncoder.encode("aluno123"),
                    "ROLE_STUDENT",
                    student
            ));
        }

        return toResponse(student);
    }

    @Transactional
    public StudentResponse update(String studentNumber, UpdateStudentRequest request) {
        Student student = students.findByStudentNumberIgnoreCase(studentNumber.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Estudante nao encontrado."));

        String email = request.institutionalEmail().trim().toLowerCase(Locale.ROOT);
        if (!student.getInstitutionalEmail().equalsIgnoreCase(email)
                && students.existsByInstitutionalEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Ja existe um estudante com esse email.");
        }

        String courseCode = request.degreeCourseCode().trim();
        DegreeCourse course = courses.findByCodeIgnoreCase(courseCode)
                .orElseThrow(() -> new ResourceNotFoundException("Curso nao encontrado. Regista primeiro o curso."));

        student.setName(request.name().trim());
        student.setInstitutionalEmail(email);
        student.setEnrolledYear(request.enrolledYear());
        student.setDegreeCourse(course);

        return toResponse(students.save(student));
    }

    private StudentResponse toResponse(Student student) {
        DegreeCourse course = student.getDegreeCourse();
        return new StudentResponse(student.getStudentNumber(), student.getName(),
                student.getInstitutionalEmail(), student.getEnrolledYear(),
                course.getCode(), course.getName());
    }
}