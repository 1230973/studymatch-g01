
package pt.studymatch.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import pt.studymatch.backend.dto.ColdStartAnswerRequest;
import pt.studymatch.backend.dto.ColdStartAnswerResponse;
import pt.studymatch.backend.model.ColdStartAnswer;
import pt.studymatch.backend.model.Competency;
import pt.studymatch.backend.model.Student;
import pt.studymatch.backend.repository.ColdStartAnswerRepository;
import pt.studymatch.backend.repository.CompetencyRepository;
import pt.studymatch.backend.repository.StudentRepository;
import pt.studymatch.backend.service.ColdStartService;

@ExtendWith(MockitoExtension.class)
class ColdStartServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CompetencyRepository competencyRepository;

    @Mock
    private ColdStartAnswerRepository answerRepository;

    @InjectMocks
    private ColdStartService coldStartService;

    @Test
    void shouldSaveValidAnswerAndCalculateInitialScore() {

        // Preparar o estudante
        Student student = org.mockito.Mockito.mock(Student.class);

        when(studentRepository.findByStudentNumberIgnoreCase("2026002"))
                .thenReturn(Optional.of(student));

        // Preparar a competencia
        Competency competency = new Competency(
                "PROGRAMMING",
                "Programacao"
        );

        when(competencyRepository.findByCode("PROGRAMMING"))
                .thenReturn(Optional.of(competency));

        // Ainda nao existe resposta anterior
        when(answerRepository.findByStudentAndCompetency(
                student, competency))
                .thenReturn(Optional.empty());

        // Simular a operacao de guardar
        when(answerRepository.save(any(ColdStartAnswer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Resposta do estudante: 4/5
        ColdStartAnswerRequest request =
                new ColdStartAnswerRequest("PROGRAMMING", 4);

        // Executar
        ColdStartAnswerResponse response =
                coldStartService.saveAnswer("2026002", request);

        // Verificar resultados
        assertEquals("PROGRAMMING", response.competencyCode());
        assertEquals(4, response.rating());
        assertEquals(75, response.initialScore());

        // Confirmar que foi enviado para guardar
        verify(answerRepository).save(any(ColdStartAnswer.class));
    }
    
    @Test
    void shouldRejectInvalidRatings() {

        // Testar respostas fora do intervalo permitido
        int[] invalidRatings = {0, 6};

        for (int rating : invalidRatings) {

            ColdStartAnswerRequest request =
                    new ColdStartAnswerRequest("PROGRAMMING", rating);

            // Confirmar que a resposta e rejeitada
            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> coldStartService.saveAnswer("2026002", request)
            );

            // Confirmar que devolve HTTP 400
            assertEquals(
                    HttpStatus.BAD_REQUEST,
                    exception.getStatusCode()
            );
        }

        // Nenhuma resposta invalida deve chegar a base de dados
        verifyNoInteractions(
                studentRepository,
                competencyRepository,
                answerRepository
        );
    }

    
    @Test
    void shouldUpdateExistingAnswerWithoutCreatingDuplicate() {

        // Preparar o estudante
        Student student = org.mockito.Mockito.mock(Student.class);

        when(studentRepository.findByStudentNumberIgnoreCase("2026002"))
                .thenReturn(Optional.of(student));

        // Preparar a competencia
        Competency competency = new Competency(
                "PROGRAMMING",
                "Programacao"
        );

        when(competencyRepository.findByCode("PROGRAMMING"))
                .thenReturn(Optional.of(competency));

        // Criar uma resposta que ja existe: 2/5
        ColdStartAnswer existingAnswer = new ColdStartAnswer(
                student,
                competency,
                2
        );

        when(answerRepository.findByStudentAndCompetency(
                student, competency))
                .thenReturn(Optional.of(existingAnswer));

        // Simular a operacao de guardar
        when(answerRepository.save(existingAnswer))
                .thenReturn(existingAnswer);

        // Nova resposta: 5/5
        ColdStartAnswerRequest request =
                new ColdStartAnswerRequest("PROGRAMMING", 5);

        // Executar a atualizacao
        ColdStartAnswerResponse response =
                coldStartService.saveAnswer("2026002", request);

        // Verificar a nova resposta
        assertEquals("PROGRAMMING", response.competencyCode());
        assertEquals(5, response.rating());
        assertEquals(100, response.initialScore());

        // Confirmar que o objeto existente foi atualizado
        assertEquals(5, existingAnswer.getRating());

        // Confirmar que foi guardado o mesmo objeto
        verify(answerRepository).save(existingAnswer);

        assertSame(
                existingAnswer,
                answerRepository.save(existingAnswer)
        );
    }


}
