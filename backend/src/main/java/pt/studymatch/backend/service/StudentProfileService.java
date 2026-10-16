
package pt.studymatch.backend.service;

import java.util.List;
import java.util.OptionalDouble;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import pt.studymatch.backend.dto.CompetencyScoreResponse;
import pt.studymatch.backend.dto.StudentProfileResponse;
import pt.studymatch.backend.model.AcademicRecord;
import pt.studymatch.backend.model.Competency;
import pt.studymatch.backend.model.Student;
import pt.studymatch.backend.repository.AcademicRecordRepository;
import pt.studymatch.backend.repository.CompetencyRepository;
import pt.studymatch.backend.repository.StudentRepository;

@Service
public class StudentProfileService {

    private final StudentRepository studentRepository;
    private final AcademicRecordRepository academicRecordRepository;
    private final CompetencyRepository competencyRepository;

    public StudentProfileService(
            StudentRepository studentRepository,
            AcademicRecordRepository academicRecordRepository,
            CompetencyRepository competencyRepository) {

        this.studentRepository = studentRepository;
        this.academicRecordRepository = academicRecordRepository;
        this.competencyRepository = competencyRepository;
    }

    @Transactional(readOnly = true)
    public StudentProfileResponse getStudentProfile(String studentNumber) {

        // Procurar o estudante
        Student student = studentRepository
                .findByStudentNumberIgnoreCase(studentNumber)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Estudante nao encontrado"
                ));

        // Consultar o historico academico
        List<AcademicRecord> records =
                academicRecordRepository.findByStudentOrderByIdAsc(student);

        // Consultar as competencias disponiveis
        List<Competency> competencies =
                competencyRepository.findAll(Sort.by("code"));

        // Calcular cada competencia
        List<CompetencyScoreResponse> scores = competencies.stream()
                .map(competency -> calculateScore(competency, records))
                .toList();

        return new StudentProfileResponse(
                student.getStudentNumber(),
                scores
        );
    }

    private CompetencyScoreResponse calculateScore(
            Competency competency,
            List<AcademicRecord> records) {

        // Selecionar as notas associadas a esta competencia
        List<AcademicRecord> matchingRecords = records.stream()
                .filter(academicRecord ->
                        academicRecord.getCourseUnit() != null
                        && academicRecord.getCourseUnit().getCompetency() != null
                        && academicRecord.getGrade() != null
                        && competency.getId().equals(
                                academicRecord.getCourseUnit()
                                        .getCompetency().getId()
                        )
                )
                .toList();

        // Sem notas = competencia desconhecida
        if (matchingRecords.isEmpty()) {
            return new CompetencyScoreResponse(
                    competency.getCode(),
                    competency.getName(),
                    null,
                    0
            );
        }

        // Calcular a media das classificacoes
        OptionalDouble average = matchingRecords.stream()
                .mapToDouble(academicRecord ->
                        academicRecord.getGrade().doubleValue()
                )
                .average();

        // Converter a media da escala 0-20 para 0-100
        double score = average.orElseThrow() * 5;

        // Arredondar a duas casas decimais
        score = Math.round(score * 100.0) / 100.0;

        return new CompetencyScoreResponse(
                competency.getCode(),
                competency.getName(),
                score,
                matchingRecords.size()
        );
    }
}
