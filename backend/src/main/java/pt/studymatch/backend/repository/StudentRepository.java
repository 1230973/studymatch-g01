package pt.studymatch.backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.studymatch.backend.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentNumberIgnoreCase(String studentNumber);
    boolean existsByStudentNumberIgnoreCase(String studentNumber);
    boolean existsByInstitutionalEmailIgnoreCase(String institutionalEmail);
    List<Student> findAllByOrderByStudentNumberAsc();
}
