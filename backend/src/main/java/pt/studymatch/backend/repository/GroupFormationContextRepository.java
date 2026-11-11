
package pt.studymatch.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.studymatch.backend.model.GroupFormationContext;

public interface GroupFormationContextRepository
        extends JpaRepository<GroupFormationContext, Long> {

}
