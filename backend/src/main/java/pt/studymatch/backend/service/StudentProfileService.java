
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
import pt.studymatch.backend.model.ColdStartAnswer;
import pt.studymatch.backend.model.Competency;
import pt.studymatch.backend.model.Student;
import pt.studymatch.backend.repository.AcademicRecordRepository;
import pt.studymatch.backend.repository.ColdStartAnswerRepository;
import pt.studymatch.backend.repository.CompetencyRepository;
import pt.studymatch.backend.repository.StudentRepository;

@Service
public class StudentProfileService {

    private final StudentRepository studentRepository;
    private final AcademicRecordRepository academicRecordRepository;
    private final CompetencyRepository competencyRepository;
    private final ColdStartAnswerRepository coldStartAnswerRepository;

    public StudentProfileService(
            StudentRepository studentRepository,
            AcademicRecordRepository academicRecordRepository,
            CompetencyRepository competencyRepository,
            ColdStartAnswerRepository coldStartAnswerRepository) {

        this.studentRepository = studentRepository;
        this.academicRecordRepository = academicRecordRepository;
        this.competencyRepository = competencyRepository;
        this.coldStartAnswerRepository = coldStartAnswerRepository;
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

        // Consultar as notas
        List<AcademicRecord> records =
                academicRecordRepository.findByStudentOrderByIdAsc(student);

        // Consultar as respostas Cold Start
        List<ColdStartAnswer> answers =
                coldStartAnswerRepository.findByStudent(student);

        // Consultar as competencias
        List<Competency> competencies =
                competencyRepository.findAll(Sort.by("code"));

        // Calcular o perfil
        List<CompetencyScoreResponse> scores = competencies.stream()
                .map(competency ->
                        calculateScore(competency, records, answers)
                )
                .toList();

        return new StudentProfileResponse(
                student.getStudentNumber(),
                scores
        );
    }

    private CompetencyScoreResponse calculateScore(
            Competency competency,
            List<AcademicRecord> records,
            List<ColdStartAnswer> answers) {

        // Procurar notas associadas a esta competencia
        List<AcademicRecord> matchingRecords = records.stream()
                .filter(academicRecord ->
                        academicRecord.getCourseUnit() != null
                        && academicRecord.getCourseUnit().getCompetency() != null
                        && academicRecord.getGrade() != null
                        && competency.getCode().equals(
                                academicRecord.getCourseUnit()
                                        .getCompetency().getCode()
                        )
                )
                .toList();

        // PRIORIDADE 1: notas academicas
        if (!matchingRecords.isEmpty()) {

            OptionalDouble average = matchingRecords.stream()
                    .mapToDouble(academicRecord ->
                            academicRecord.getGrade().doubleValue()
                    )
                    .average();

            double score = average.orElseThrow() * 5;
            score = Math.round(score * 100.0) / 100.0;

            return new CompetencyScoreResponse(
                    competency.getCode(),
                    competency.getName(),
                    score,
                    matchingRecords.size(),
                    "ACADEMIC"
            );
        }

        // PRIORIDADE 2: resposta Cold Start
        for (ColdStartAnswer answer : answers) {

            if (answer.getCompetency() != null
                    && competency.getCode().equals(
                            answer.getCompetency().getCode()
                    )) {

                double score = (answer.getRating() - 1) * 25.0;

                return new CompetencyScoreResponse(
                        competency.getCode(),
                        competency.getName(),
                        score,
                        0,
                        "COLD_START"
                );
            }
        }

        // PRIORIDADE 3: sem informacao
        return new CompetencyScoreResponse(
                competency.getCode(),
                competency.getName(),
                null,
                0,
                "UNKNOWN"
        );
    }
}
