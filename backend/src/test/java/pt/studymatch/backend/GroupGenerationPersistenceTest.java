
package pt.studymatch.backend;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import pt.studymatch.backend.dto.GroupGenerationResponse;
import pt.studymatch.backend.model.ContextParticipant;
import pt.studymatch.backend.model.DegreeCourse;
import pt.studymatch.backend.model.GroupFormationContext;
import pt.studymatch.backend.model.Student;
import pt.studymatch.backend.repository.ContextParticipantRepository;
import pt.studymatch.backend.repository.DegreeCourseRepository;
import pt.studymatch.backend.repository.GroupFormationContextRepository;
import pt.studymatch.backend.repository.GroupGenerationRepository;
import pt.studymatch.backend.repository.GroupMembershipRepository;
import pt.studymatch.backend.repository.StudentRepository;
import pt.studymatch.backend.service.GroupGenerationService;
import pt.studymatch.backend.service.PlannedGroup;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GroupGenerationPersistenceTest {

    @Autowired
    private GroupGenerationService service;

    @Autowired
    private DegreeCourseRepository courses;

    @Autowired
    private StudentRepository students;

    @Autowired
    private GroupFormationContextRepository contexts;

    @Autowired
    private ContextParticipantRepository participants;

    @Autowired
    private GroupGenerationRepository generations;

    @Autowired
    private GroupMembershipRepository memberships;

    @Test
    void savesGroupsWithFiveAndFourStudents() {

        Setup setup = createStudents(9);
        List<String> numbers = setup.numbers();

        List<PlannedGroup> planned = List.of(
                new PlannedGroup(
                        numbers.subList(0, 5),
                        80.0,
                        "Grupo equilibrado"),
                new PlannedGroup(
                        numbers.subList(5, 9),
                        75.0,
                        "Competencias complementares")
        );

        GroupGenerationResponse result =
                service.saveFromMatching(
                        setup.contextId(),
                        planned,
                        "1.0",
                        "BALANCED_COMPETENCIES",
                        null
                );

        assertNotNull(result.id());
        assertEquals(2, result.groups().size());
        assertEquals(5, result.groups().get(0).members().size());
        assertEquals(4, result.groups().get(1).members().size());

        assertEquals(9,
                memberships.findByGenerationId(result.id()).size());

        assertEquals(1,
                service.list(setup.contextId()).size());

        GroupGenerationResponse loaded =
                service.find(setup.contextId(), result.id());

        assertEquals(2, loaded.groups().size());
    }

    @Test
    void preservesPreviousGenerations() {

        Setup setup = createStudents(4);

        GroupGenerationResponse first =
                service.saveFromMatching(
                        setup.contextId(),
                        List.of(new PlannedGroup(
                                setup.numbers(),
                                80.0,
                                "Primeira geracao")),
                        "1.0",
                        "BALANCED",
                        null
                );

        GroupGenerationResponse second =
                service.saveFromMatching(
                        setup.contextId(),
                        List.of(new PlannedGroup(
                                new ArrayList<>(setup.numbers()),
                                70.0,
                                "Segunda geracao")),
                        "1.0",
                        "BALANCED",
                        null
                );

        assertNotEquals(first.id(), second.id());

        assertEquals(2,
                service.list(setup.contextId()).size());

        GroupGenerationResponse original =
                service.find(setup.contextId(), first.id());

        assertEquals("Primeira geracao",
                original.groups().get(0).explanation());

        assertEquals(4,
                original.groups().get(0).members().size());
    }

    @Test
    void rejectsDuplicatedStudentsWithoutSaving() {

        Setup setup = createStudents(8);
        List<String> numbers = setup.numbers();

        List<PlannedGroup> invalid = List.of(
                new PlannedGroup(
                        numbers.subList(0, 4),
                        null,
                        "Grupo 1"),
                new PlannedGroup(
                        numbers.subList(3, 7),
                        null,
                        "Grupo 2")
        );

        assertThrows(IllegalArgumentException.class,
                () -> service.saveFromMatching(
                        setup.contextId(),
                        invalid,
                        "1.0",
                        "BALANCED",
                        null
                ));

        assertTrue(generations
                .findByContextIdOrderByGeneratedAtDesc(
                        setup.contextId())
                .isEmpty());
    }

    @Test
    void rejectsGroupsWithInvalidSize() {

        Setup setup = createStudents(6);

        assertThrows(IllegalArgumentException.class,
                () -> service.saveFromMatching(
                        setup.contextId(),
                        List.of(new PlannedGroup(
                                setup.numbers(),
                                null,
                                "Grupo invalido")),
                        "1.0",
                        "BALANCED",
                        null
                ));

        assertTrue(generations
                .findByContextIdOrderByGeneratedAtDesc(
                        setup.contextId())
                .isEmpty());
    }

    private Setup createStudents(int amount) {

        DegreeCourse course = courses.save(
                new DegreeCourse(
                        "S3EI",
                        "Engenharia Informatica Sprint 3",
                        180
                ));

        GroupFormationContext context = contexts.save(
                new GroupFormationContext(
                        "Trabalho de Programacao",
                        "Teste de persistencia",
                        "professor-teste"
                ));

        List<String> numbers = new ArrayList<>();

        for (int i = 1; i <= amount; i++) {

            String number = String.format("S3%04d", i);

            Student student = students.save(
                    new Student(
                            number,
                            "Estudante " + i,
                            "sprint3-" + i + "@example.org",
                            1,
                            course
                    ));

            participants.save(
                    new ContextParticipant(context, student));

            numbers.add(number);
        }

        return new Setup(context.getId(), numbers);
    }

    private record Setup(
            Long contextId,
            List<String> numbers
    ) {
    }
}
