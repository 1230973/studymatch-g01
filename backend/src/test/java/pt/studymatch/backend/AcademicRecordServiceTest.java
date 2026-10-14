
package pt.studymatch.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import pt.studymatch.backend.dto.AcademicRecordResponse;
import pt.studymatch.backend.dto.CreateAcademicRecordRequest;
import pt.studymatch.backend.model.AcademicRecord;
import pt.studymatch.backend.model.CourseUnit;
import pt.studymatch.backend.model.Student;
import pt.studymatch.backend.repository.AcademicRecordRepository;
import pt.studymatch.backend.repository.CourseUnitRepository;
import pt.studymatch.backend.repository.StudentRepository;
import pt.studymatch.backend.service.AcademicRecordService;

@ExtendWith(MockitoExtension.class)
class AcademicRecordServiceTest {

    @Mock
    private AcademicRecordRepository academicRecords;

    @Mock
    private StudentRepository students;

    @Mock
    private CourseUnitRepository courseUnits;

    @InjectMocks
    private AcademicRecordService service;

    // Teste 1: uma nota valida deve ser guardada
    @Test
    void shouldCreateValidAcademicRecord() {

        Student student = mock(Student.class);
        CourseUnit courseUnit = new CourseUnit("PROG", "Programacao");

        when(students.findByStudentNumberIgnoreCase("2026001"))
                .thenReturn(Optional.of(student));

        when(courseUnits.findByCode("PROG"))
                .thenReturn(Optional.of(courseUnit));

        when(academicRecords.save(any(AcademicRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateAcademicRecordRequest request =
                new CreateAcademicRecordRequest(
                        "PROG",
                        new BigDecimal("16"),
                        "2025/2026",
                        1
                );

        AcademicRecordResponse response =
                service.create("2026001", request);

        assertEquals("PROG", response.courseUnitCode());
        assertEquals(0, new BigDecimal("16").compareTo(response.grade()));

        verify(academicRecords).save(any(AcademicRecord.class));
    }

    // Teste 2: nota superior a 20 deve ser rejeitada
    @Test
    void shouldRejectGradeAbove20() {

        CreateAcademicRecordRequest request =
                new CreateAcademicRecordRequest(
                        "PROG",
                        new BigDecimal("25"),
                        "2025/2026",
                        1
                );

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> service.create("2026001", request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

        verifyNoInteractions(academicRecords);
    }

    // Teste 3: nota negativa deve ser rejeitada
    @Test
    void shouldRejectNegativeGrade() {

        CreateAcademicRecordRequest request =
                new CreateAcademicRecordRequest(
                        "PROG",
                        new BigDecimal("-1"),
                        "2025/2026",
                        1
                );

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> service.create("2026001", request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

        verifyNoInteractions(academicRecords);
    }

    // Teste 4: os limites 0 e 20 devem ser aceites
    @ParameterizedTest
    @ValueSource(strings = {"0", "20"})
    void shouldAcceptBoundaryGrades(String grade) {

        Student student = mock(Student.class);
        CourseUnit courseUnit = new CourseUnit("PROG", "Programacao");

        when(students.findByStudentNumberIgnoreCase("2026001"))
                .thenReturn(Optional.of(student));

        when(courseUnits.findByCode("PROG"))
                .thenReturn(Optional.of(courseUnit));

        when(academicRecords.save(any(AcademicRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateAcademicRecordRequest request =
                new CreateAcademicRecordRequest(
                        "PROG",
                        new BigDecimal(grade),
                        "2025/2026",
                        1
                );

        AcademicRecordResponse response =
                service.create("2026001", request);

        assertEquals(0, new BigDecimal(grade).compareTo(response.grade()));

        verify(academicRecords).save(any(AcademicRecord.class));
    }
}
