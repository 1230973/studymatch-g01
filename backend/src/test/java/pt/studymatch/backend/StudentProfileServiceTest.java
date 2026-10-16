
package pt.studymatch.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import pt.studymatch.backend.dto.StudentProfileResponse;
import pt.studymatch.backend.model.AcademicRecord;
import pt.studymatch.backend.model.Competency;
import pt.studymatch.backend.model.CourseUnit;
import pt.studymatch.backend.model.Student;
import pt.studymatch.backend.repository.AcademicRecordRepository;
import pt.studymatch.backend.repository.CompetencyRepository;
import pt.studymatch.backend.repository.StudentRepository;
import pt.studymatch.backend.service.StudentProfileService;






@ExtendWith(MockitoExtension.class)
class StudentProfileServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private AcademicRecordRepository academicRecordRepository;

    @Mock
    private CompetencyRepository competencyRepository;

    @InjectMocks
    private StudentProfileService studentProfileService;

    @Test
    void shouldCalculateProgrammingScoreFromGrade16() {

        // Preparar o estudante
        Student student = org.mockito.Mockito.mock(Student.class);

        when(student.getStudentNumber()).thenReturn("2026001");

        when(studentRepository.findByStudentNumberIgnoreCase("2026001"))
                .thenReturn(java.util.Optional.of(student));

        // Preparar a competencia
        Competency competency =
                org.mockito.Mockito.mock(Competency.class);

        when(competency.getId()).thenReturn(1L);
        when(competency.getCode()).thenReturn("PROGRAMMING");
        when(competency.getName()).thenReturn("Programacao");

        when(competencyRepository.findAll(Sort.by("code")))
                .thenReturn(List.of(competency));

        // Preparar a unidade curricular
        CourseUnit courseUnit = new CourseUnit(
                "PROG",
                "Programacao"
        );

        courseUnit.setCompetency(competency);

        // Preparar a classificacao
        AcademicRecord academicRecord =
                org.mockito.Mockito.mock(AcademicRecord.class);

        when(academicRecord.getCourseUnit())
                .thenReturn(courseUnit);

        when(academicRecord.getGrade())
                .thenReturn(new BigDecimal("16"));

        when(academicRecordRepository.findByStudentOrderByIdAsc(student))
                .thenReturn(List.of(academicRecord));

        // Executar o calculo
        StudentProfileResponse profile =
                studentProfileService.getStudentProfile("2026001");

        // Verificar os resultados
        assertEquals("2026001", profile.studentNumber());

        assertEquals(1, profile.competencies().size());

        assertEquals(
                "PROGRAMMING",
                profile.competencies().get(0).code()
        );

        assertEquals(
                80.0,
                profile.competencies().get(0).score()
        );

        assertEquals(
                1,
                profile.competencies().get(0).evidenceCount()
        );
}
    
        @Test
        void shouldReturnUnknownScoreWhenStudentHasNoAcademicHistory() {

        // Preparar o estudante
        Student student = org.mockito.Mockito.mock(Student.class);

        when(student.getStudentNumber()).thenReturn("2026002");

        when(studentRepository.findByStudentNumberIgnoreCase("2026002"))
                .thenReturn(java.util.Optional.of(student));

        // Preparar a competencia
        Competency competency = new Competency(
                "PROGRAMMING",
                "Programacao"
        );

        when(competencyRepository.findAll(Sort.by("code")))
                .thenReturn(List.of(competency));

        // Estudante sem classificacoes
        when(academicRecordRepository.findByStudentOrderByIdAsc(student))
                .thenReturn(List.of());

        // Calcular o perfil
        StudentProfileResponse profile =
                studentProfileService.getStudentProfile("2026002");

        // Verificar o resultado
        assertEquals("2026002", profile.studentNumber());

        assertEquals(1, profile.competencies().size());

        assertEquals(
                "PROGRAMMING",
                profile.competencies().get(0).code()
        );

        assertNull(profile.competencies().get(0).score());

        assertEquals(
                0,
                profile.competencies().get(0).evidenceCount()
        );
        }
        
        
        @Test
        void shouldCalculateAverageScoreFromTwoGrades() {

        // Preparar o estudante
        Student student = org.mockito.Mockito.mock(Student.class);

        when(student.getStudentNumber()).thenReturn("2026003");

        when(studentRepository.findByStudentNumberIgnoreCase("2026003"))
                .thenReturn(java.util.Optional.of(student));

        // Preparar a competencia
        Competency competency = org.mockito.Mockito.mock(Competency.class);

        when(competency.getId()).thenReturn(1L);
        when(competency.getCode()).thenReturn("PROGRAMMING");
        when(competency.getName()).thenReturn("Programacao");

        when(competencyRepository.findAll(Sort.by("code")))
                .thenReturn(List.of(competency));

        // Preparar a unidade curricular
        CourseUnit courseUnit = new CourseUnit("PROG", "Programacao");
        courseUnit.setCompetency(competency);

        // Primeira classificacao: 12
        AcademicRecord firstRecord =
                org.mockito.Mockito.mock(AcademicRecord.class);

        when(firstRecord.getCourseUnit()).thenReturn(courseUnit);
        when(firstRecord.getGrade()).thenReturn(new BigDecimal("12"));

        // Segunda classificacao: 18
        AcademicRecord secondRecord =
                org.mockito.Mockito.mock(AcademicRecord.class);

        when(secondRecord.getCourseUnit()).thenReturn(courseUnit);
        when(secondRecord.getGrade()).thenReturn(new BigDecimal("18"));

        // Devolver as duas classificacoes
        when(academicRecordRepository.findByStudentOrderByIdAsc(student))
                .thenReturn(List.of(firstRecord, secondRecord));

        // Calcular o perfil
        StudentProfileResponse profile =
                studentProfileService.getStudentProfile("2026003");

        // Verificar os resultados
        assertEquals("2026003", profile.studentNumber());

        assertEquals(
                75.0,
                profile.competencies().get(0).score()
        );

        assertEquals(
                2,
                profile.competencies().get(0).evidenceCount()
        );
        }

        
        @Test
        void shouldReturnNotFoundWhenStudentDoesNotExist() {

        // Simular um estudante que nao existe
        when(studentRepository.findByStudentNumberIgnoreCase("9999999"))
                .thenReturn(Optional.empty());

        // Tentar consultar o perfil
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> studentProfileService.getStudentProfile("9999999")
        );

        // Verificar se devolve HTTP 404
        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );
        }


}
