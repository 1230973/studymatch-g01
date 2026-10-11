package pt.studymatch.backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.studymatch.backend.model.DegreeCourse;

public interface DegreeCourseRepository extends JpaRepository<DegreeCourse, Long> {
    Optional<DegreeCourse> findByCodeIgnoreCase(String code);
    boolean existsByCodeIgnoreCase(String code);
    List<DegreeCourse> findAllByOrderByCodeAsc();
}
