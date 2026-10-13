
package pt.studymatch.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pt.studymatch.backend.model.AcademicRecord;
import pt.studymatch.backend.model.Student;

public interface AcademicRecordRepository
        extends JpaRepository<AcademicRecord, Long> {

    List<AcademicRecord> findByStudentOrderByIdAsc(Student student);
}
