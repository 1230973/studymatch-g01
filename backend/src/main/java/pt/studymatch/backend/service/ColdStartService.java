
package pt.studymatch.backend.service;

import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import pt.studymatch.backend.dto.ColdStartAnswerRequest;
import pt.studymatch.backend.dto.ColdStartAnswerResponse;
import pt.studymatch.backend.model.ColdStartAnswer;
import pt.studymatch.backend.model.Competency;
import pt.studymatch.backend.model.Student;
import pt.studymatch.backend.repository.ColdStartAnswerRepository;
import pt.studymatch.backend.repository.CompetencyRepository;
import pt.studymatch.backend.repository.StudentRepository;

@Service
public class ColdStartService {

    private final StudentRepository studentRepository;
    private final CompetencyRepository competencyRepository;
    private final ColdStartAnswerRepository answerRepository;

    public ColdStartService(
            StudentRepository studentRepository,
            CompetencyRepository competencyRepository,
            ColdStartAnswerRepository answerRepository) {

        this.studentRepository = studentRepository;
        this.competencyRepository = competencyRepository;
        this.answerRepository = answerRepository;
    }

    @Transactional
    public ColdStartAnswerResponse saveAnswer(
            String studentNumber,
            ColdStartAnswerRequest request) {

        // Validar os dados recebidos
        if (request == null
                || request.competencyCode() == null
                || request.competencyCode().isBlank()
                || request.rating() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Dados da resposta obrigatorios"
            );
        }

        // Validar a resposta entre 1 e 5
        int rating = request.rating();

        if (rating < 1 || rating > 5) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A resposta deve estar entre 1 e 5"
            );
        }

        // Procurar o estudante
        Student student = findStudent(studentNumber);

        // Procurar a competencia
        String code = request.competencyCode()
                .trim()
                .toUpperCase(Locale.ROOT);

        Competency competency = competencyRepository
                .findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Competencia nao encontrada"
                ));

        // Procurar uma resposta anterior
        ColdStartAnswer answer = answerRepository
                .findByStudentAndCompetency(student, competency)
                .orElse(null);

        // Criar ou atualizar a resposta
        if (answer == null) {
            answer = new ColdStartAnswer(
                    student,
                    competency,
                    rating
            );
        } else {
            answer.setRating(rating);
        }

        // Guardar no PostgreSQL
        ColdStartAnswer savedAnswer = answerRepository.save(answer);

        return toResponse(savedAnswer);
    }

    @Transactional(readOnly = true)
    public List<ColdStartAnswerResponse> getAnswers(String studentNumber) {

        Student student = findStudent(studentNumber);

        return answerRepository.findByStudent(student)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private Student findStudent(String studentNumber) {

        if (studentNumber == null || studentNumber.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Numero de estudante obrigatorio"
            );
        }

        return studentRepository
                .findByStudentNumberIgnoreCase(studentNumber)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Estudante nao encontrado"
                ));
    }

    private ColdStartAnswerResponse toResponse(ColdStartAnswer answer) {

        int rating = answer.getRating();

        // Converter a resposta para uma escala de 0 a 100
        int initialScore = (rating - 1) * 25;

        return new ColdStartAnswerResponse(
                answer.getId(),
                answer.getCompetency().getCode(),
                rating,
                initialScore
        );
    }
}
