
package pt.studymatch.backend.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import pt.studymatch.backend.dto.ContextParticipantResponse;
import pt.studymatch.backend.dto.UpdateContextParticipantsRequest;
import pt.studymatch.backend.exception.ResourceNotFoundException;
import pt.studymatch.backend.model.ContextParticipant;
import pt.studymatch.backend.model.GroupFormationContext;
import pt.studymatch.backend.model.Student;
import pt.studymatch.backend.repository.ContextParticipantRepository;
import pt.studymatch.backend.repository.GroupFormationContextRepository;
import pt.studymatch.backend.repository.StudentRepository;

@Service
public class ContextParticipantService {

    private final ContextParticipantRepository participants;
    private final GroupFormationContextRepository contexts;
    private final StudentRepository students;

    public ContextParticipantService(
            ContextParticipantRepository participants,
            GroupFormationContextRepository contexts,
            StudentRepository students) {

        this.participants = participants;
        this.contexts = contexts;
        this.students = students;
    }

    @Transactional
    public List<ContextParticipantResponse> replace(
            Long contextId,
            UpdateContextParticipantsRequest request) {

        GroupFormationContext context = findContext(contextId);

        Set<String> usedNumbers = new HashSet<>();
        List<ContextParticipant> selected = new ArrayList<>();

        // Validar todos antes de alterar a base de dados.
        for (String number : request.studentNumbers()) {

            if (number == null || number.isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Numero de estudante invalido.");
            }

            String normalized = number.trim()
                    .toUpperCase(Locale.ROOT);

            if (!usedNumbers.add(normalized)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Existem estudantes repetidos na selecao.");
            }

            Student student = students
                    .findByStudentNumberIgnoreCase(normalized)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Estudante nao encontrado: "
                                            + normalized));

            selected.add(
                    new ContextParticipant(context, student)
            );
        }

        // Substituir a selecao anterior.
        participants.deleteByContextId(contextId);
        participants.flush();

        List<ContextParticipant> saved =
                participants.saveAll(selected);

        return toResponses(saved);
    }

    @Transactional(readOnly = true)
    public List<ContextParticipantResponse> list(Long contextId) {

        findContext(contextId);

        return toResponses(
                participants.findByContextId(contextId)
        );
    }

    private GroupFormationContext findContext(Long contextId) {

        return contexts.findById(contextId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Contexto nao encontrado."));
    }

    private List<ContextParticipantResponse> toResponses(
            List<ContextParticipant> records) {

        return records.stream()
                .sorted(Comparator.comparing(
                        p -> p.getStudent().getStudentNumber(),
                        String.CASE_INSENSITIVE_ORDER
                ))
                .map(p -> new ContextParticipantResponse(
                        p.getStudent().getStudentNumber(),
                        p.getStudent().getName()
                ))
                .toList();
    }
}
