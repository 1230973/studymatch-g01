
package pt.studymatch.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pt.studymatch.backend.model.ContextParticipant;

public interface ContextParticipantRepository
        extends JpaRepository<ContextParticipant, Long> {

    List<ContextParticipant> findByContextId(Long contextId);

    boolean existsByContextIdAndStudentId(
            Long contextId, Long studentId);

    void deleteByContextId(Long contextId);
}
