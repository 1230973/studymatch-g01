
package pt.studymatch.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyLong;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import pt.studymatch.backend.dto.UpdateContextParticipantsRequest;
import pt.studymatch.backend.model.GroupFormationContext;
import pt.studymatch.backend.repository.ContextParticipantRepository;
import pt.studymatch.backend.repository.GroupFormationContextRepository;
import pt.studymatch.backend.repository.StudentRepository;
import pt.studymatch.backend.service.ContextParticipantService;

@ExtendWith(MockitoExtension.class)
class ContextParticipantServiceTest {

    @Mock
    private ContextParticipantRepository participants;

    @Mock
    private GroupFormationContextRepository contexts;

    @Mock
    private StudentRepository students;

    @InjectMocks
    private ContextParticipantService service;

    @Test
    void rejectsDuplicateStudents() {

        GroupFormationContext context =
                new GroupFormationContext("Teste", null, null);

        when(contexts.findById(1L))
                .thenReturn(Optional.of(context));

        UpdateContextParticipantsRequest request =
                new UpdateContextParticipantsRequest(
                        List.of("2026001", " 2026001 ")
                );

        // O primeiro estudante precisa de existir.
        // A segunda ocorrencia sera rejeitada.
        when(students.findByStudentNumberIgnoreCase("2026001"))
                .thenReturn(Optional.of(
                        new pt.studymatch.backend.model.Student(
                                "2026001",
                                "Ana",
                                "ana@exemplo.pt",
                                1,
                                null
                        )
                ));

        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.replace(1L, request)
        );

        assertEquals(400, error.getStatusCode().value());

        // Uma selecao invalida nao deve apagar participantes.
        verify(participants, never()).deleteByContextId(anyLong());
    }
}
