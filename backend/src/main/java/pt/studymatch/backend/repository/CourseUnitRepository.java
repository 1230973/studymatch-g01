
package pt.studymatch.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pt.studymatch.backend.model.CourseUnit;

public interface CourseUnitRepository extends JpaRepository<CourseUnit, Long> {

    Optional<CourseUnit> findByCode(String code);

    boolean existsByCode(String code);
}
