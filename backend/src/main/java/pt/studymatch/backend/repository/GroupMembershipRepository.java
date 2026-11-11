
package pt.studymatch.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pt.studymatch.backend.model.GroupMembership;

public interface GroupMembershipRepository
        extends JpaRepository<GroupMembership, Long> {

    List<GroupMembership> findByStudentGroupId(Long groupId);

    List<GroupMembership> findByGenerationId(Long generationId);

    boolean existsByGenerationIdAndStudentId(
            Long generationId, Long studentId);
}
