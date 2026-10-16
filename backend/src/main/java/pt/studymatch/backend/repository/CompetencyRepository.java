
package pt.studymatch.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pt.studymatch.backend.model.Competency;

public interface CompetencyRepository extends JpaRepository<Competency, Long> {

    Optional<Competency> findByCode(String code);

    boolean existsByCode(String code);
}
