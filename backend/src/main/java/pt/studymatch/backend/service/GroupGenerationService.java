
package pt.studymatch.backend.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.studymatch.backend.dto.GroupGenerationResponse;
import pt.studymatch.backend.dto.GroupGenerationSummaryResponse;
import pt.studymatch.backend.dto.GroupMemberResponse;
import pt.studymatch.backend.dto.StudentGroupResponse;
import pt.studymatch.backend.exception.ResourceNotFoundException;
import pt.studymatch.backend.model.ContextParticipant;
import pt.studymatch.backend.model.GroupFormationContext;
import pt.studymatch.backend.model.GroupGeneration;
import pt.studymatch.backend.model.GroupMembership;
import pt.studymatch.backend.model.Student;
import pt.studymatch.backend.model.StudentGroup;
import pt.studymatch.backend.repository.ContextParticipantRepository;
import pt.studymatch.backend.repository.GroupFormationContextRepository;
import pt.studymatch.backend.repository.GroupGenerationRepository;
import pt.studymatch.backend.repository.GroupMembershipRepository;
import pt.studymatch.backend.repository.StudentGroupRepository;

@Service
public class GroupGenerationService {

    private final GroupFormationContextRepository contexts;
    private final ContextParticipantRepository participants;
    private final GroupGenerationRepository generations;
    private final StudentGroupRepository groups;
    private final GroupMembershipRepository memberships;

    public GroupGenerationService(
            GroupFormationContextRepository contexts,
            ContextParticipantRepository participants,
            GroupGenerationRepository generations,
            StudentGroupRepository groups,
            GroupMembershipRepository memberships) {

        this.contexts = contexts;
        this.participants = participants;
        this.generations = generations;
        this.groups = groups;
        this.memberships = memberships;
    }

    @Transactional(readOnly = true)
    public List<GroupGenerationSummaryResponse> list(Long contextId) {

        findContext(contextId);

        return generations
                .findByContextIdOrderByGeneratedAtDesc(contextId)
                .stream()
                .map(generation ->
                        new GroupGenerationSummaryResponse(
                                generation.getId(),
                                contextId,
                                generation.getGeneratedAt(),
                                generation.getAlgorithmVersion(),
                                generation.getStrategy(),
                                groups.findByGenerationIdOrderByGroupNumberAsc(
                                        generation.getId()
                                ).size()
                        )
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public GroupGenerationResponse find(
            Long contextId,
            Long generationId) {

        findContext(contextId);

        GroupGeneration generation = generations
                .findById(generationId)
                .filter(g -> g.getContext().getId().equals(contextId))
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Geracao nao encontrada neste contexto."
                        ));

        List<StudentGroupResponse> resultGroups =
                new ArrayList<>();

        for (StudentGroup group :
                groups.findByGenerationIdOrderByGroupNumberAsc(
                        generationId)) {

            List<GroupMemberResponse> members = memberships
                    .findByStudentGroupId(group.getId())
                    .stream()
                    .map(m -> new GroupMemberResponse(
                            m.getStudent().getStudentNumber(),
                            m.getStudent().getName()
                    ))
                    .sorted(Comparator.comparing(
                            GroupMemberResponse::studentNumber
                    ))
                    .toList();

            resultGroups.add(new StudentGroupResponse(
                    group.getId(),
                    group.getGroupNumber(),
                    group.getBalanceScore(),
                    group.getExplanation(),
                    members
            ));
        }

        return new GroupGenerationResponse(
                generation.getId(),
                contextId,
                generation.getGeneratedAt(),
                generation.getAlgorithmVersion(),
                generation.getStrategy(),
                generation.getParameters(),
                resultGroups
        );
    }

    @Transactional
    public GroupGenerationResponse saveFromMatching(
            Long contextId,
            List<PlannedGroup> plannedGroups,
            String algorithmVersion,
            String strategy,
            String parameters) {

        GroupFormationContext context = findContext(contextId);

        validateMetadata(algorithmVersion, strategy, parameters);

        if (plannedGroups == null || plannedGroups.isEmpty()) {
            throw new IllegalArgumentException(
                    "Nao foram propostos grupos."
            );
        }

        List<ContextParticipant> eligible =
                participants.findByContextId(contextId);

        Map<String, Student> eligibleStudents = new HashMap<>();

        for (ContextParticipant participant : eligible) {
            Student student = participant.getStudent();
            String number = normalize(student.getStudentNumber());

            if (eligibleStudents.putIfAbsent(number, student) != null) {
                throw new IllegalStateException(
                        "Existem participantes duplicados no contexto."
                );
            }
        }

        Set<String> assigned = new HashSet<>();

        // Validar toda a distribuicao antes de guardar.
        for (PlannedGroup planned : plannedGroups) {

            if (planned == null || planned.studentNumbers() == null) {
                throw new IllegalArgumentException(
                        "Grupo invalido."
                );
            }

            int size = planned.studentNumbers().size();

            if (size != 4 && size != 5) {
                throw new IllegalArgumentException(
                        "Cada grupo deve ter 4 ou 5 estudantes."
                );
            }

            if (planned.explanation() != null
                    && planned.explanation().length() > 2000) {
                throw new IllegalArgumentException(
                        "Explicacao demasiado longa."
                );
            }

            if (planned.balanceScore() != null
                    && !Double.isFinite(planned.balanceScore())) {
                throw new IllegalArgumentException(
                        "Indicador de equilibrio invalido."
                );
            }

            for (String studentNumber : planned.studentNumbers()) {

                String number = normalize(studentNumber);

                if (!eligibleStudents.containsKey(number)) {
                    throw new IllegalArgumentException(
                            "Estudante nao elegivel: " + number
                    );
                }

                if (!assigned.add(number)) {
                    throw new IllegalArgumentException(
                            "Estudante atribuido mais do que uma vez: "
                                    + number
                    );
                }
            }
        }

        if (!assigned.equals(eligibleStudents.keySet())) {
            throw new IllegalArgumentException(
                    "Nem todos os estudantes elegiveis foram atribuidos."
            );
        }

        // Todas as validacoes passaram.
        // A gravacao e feita numa unica transacao.
        GroupGeneration generation = generations.save(
                new GroupGeneration(
                        context,
                        algorithmVersion,
                        strategy,
                        parameters
                )
        );

        int groupNumber = 1;

        for (PlannedGroup planned : plannedGroups) {

            StudentGroup group = groups.save(
                    new StudentGroup(
                            generation,
                            groupNumber++,
                            planned.balanceScore(),
                            planned.explanation()
                    )
            );

            for (String studentNumber : planned.studentNumbers()) {

                Student student = eligibleStudents.get(
                        normalize(studentNumber)
                );

                memberships.save(
                        new GroupMembership(
                                generation,
                                group,
                                student
                        )
                );
            }
        }

        return find(contextId, generation.getId());
    }

    private void validateMetadata(
            String version,
            String strategy,
            String parameters) {

        if (version == null || version.isBlank()
                || version.length() > 50) {
            throw new IllegalArgumentException(
                    "Versao do algoritmo invalida."
            );
        }

        if (strategy == null || strategy.isBlank()
                || strategy.length() > 100) {
            throw new IllegalArgumentException(
                    "Estrategia de matching invalida."
            );
        }

        if (parameters != null && parameters.length() > 2000) {
            throw new IllegalArgumentException(
                    "Parametros demasiado longos."
            );
        }
    }

    private String normalize(String value) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Numero de estudante invalido."
            );
        }

        return value.trim().toUpperCase(Locale.ROOT);
    }

    private GroupFormationContext findContext(Long contextId) {

        return contexts.findById(contextId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Contexto nao encontrado."
                        ));
    }
}
