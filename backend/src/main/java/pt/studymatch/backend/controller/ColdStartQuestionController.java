
package pt.studymatch.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import pt.studymatch.backend.dto.ColdStartQuestionResponse;
import pt.studymatch.backend.service.CompetencyService;

@RestController
public class ColdStartQuestionController {

    private final CompetencyService competencyService;

    public ColdStartQuestionController(CompetencyService competencyService) {
        this.competencyService = competencyService;
    }

    @GetMapping("/api/cold-start/questions")
    public List<ColdStartQuestionResponse> getQuestions() {

        return competencyService.getAllCompetencies()
                .stream()
                .map(competency -> new ColdStartQuestionResponse(
                        competency.code(),
                        getQuestion(competency.code()),
                        1,
                        5
                ))
                .toList();
    }

    private String getQuestion(String code) {

        return switch (code) {
            case "PROGRAMMING" ->
                    "Qual e o teu nivel de experiencia em programacao?";

            case "DATABASES" ->
                    "Qual e o teu nivel de experiencia em bases de dados?";

            case "SOFTWARE_ENGINEERING" ->
                    "Qual e o teu nivel de experiencia em desenvolvimento de software?";

            default ->
                    "Qual e o teu nivel de experiencia nesta competencia?";
        };
    }
}
