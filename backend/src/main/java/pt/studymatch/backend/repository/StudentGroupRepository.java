
package pt.studymatch.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pt.studymatch.backend.model.StudentGroup;

public interface StudentGroupRepository
        extends JpaRepository<StudentGroup, Long> {

    List<StudentGroup> findByGenerationIdOrderByGroupNumberAsc(
            Long generationId);
}
