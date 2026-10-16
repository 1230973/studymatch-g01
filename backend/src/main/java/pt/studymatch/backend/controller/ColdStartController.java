
package pt.studymatch.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pt.studymatch.backend.dto.ColdStartAnswerRequest;
import pt.studymatch.backend.dto.ColdStartAnswerResponse;
import pt.studymatch.backend.service.ColdStartService;

@RestController
@RequestMapping("/api/students/{studentNumber}/cold-start")
public class ColdStartController {

    private final ColdStartService coldStartService;

    public ColdStartController(ColdStartService coldStartService) {
        this.coldStartService = coldStartService;
    }

    // Consultar as respostas do estudante
    @GetMapping
    public List<ColdStartAnswerResponse> getAnswers(
            @PathVariable String studentNumber) {

        return coldStartService.getAnswers(studentNumber);
    }

    // Guardar ou atualizar uma resposta
    @PostMapping
    public ColdStartAnswerResponse saveAnswer(
            @PathVariable String studentNumber,
            @RequestBody ColdStartAnswerRequest request) {

        return coldStartService.saveAnswer(studentNumber, request);
    }
}
