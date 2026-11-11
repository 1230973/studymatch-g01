
package pt.studymatch.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pt.studymatch.backend.model.GroupGeneration;

public interface GroupGenerationRepository
        extends JpaRepository<GroupGeneration, Long> {

    List<GroupGeneration> findByContextIdOrderByGeneratedAtDesc(
            Long contextId);
}
