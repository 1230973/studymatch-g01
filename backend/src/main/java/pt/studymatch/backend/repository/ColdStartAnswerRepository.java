
package pt.studymatch.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pt.studymatch.backend.model.ColdStartAnswer;
import pt.studymatch.backend.model.Competency;
import pt.studymatch.backend.model.Student;

public interface ColdStartAnswerRepository
        extends JpaRepository<ColdStartAnswer, Long> {

    List<ColdStartAnswer> findByStudent(Student student);

    Optional<ColdStartAnswer> findByStudentAndCompetency(
            Student student,
            Competency competency
    );
}
