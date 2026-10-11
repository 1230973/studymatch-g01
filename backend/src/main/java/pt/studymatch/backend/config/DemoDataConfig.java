package pt.studymatch.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pt.studymatch.backend.model.DegreeCourse;
import pt.studymatch.backend.model.Student;
import pt.studymatch.backend.repository.DegreeCourseRepository;
import pt.studymatch.backend.repository.StudentRepository;

@Configuration
@Profile("demo")
public class DemoDataConfig {
    @Bean
    CommandLineRunner demoData(DegreeCourseRepository courses, StudentRepository students) {
        return args -> {
            DegreeCourse course = courses.findByCodeIgnoreCase("EI")
                    .orElseGet(() -> courses.save(new DegreeCourse("EI", "Engenharia Informatica", 180)));
            if (!students.existsByStudentNumberIgnoreCase("2026001")) {
                students.save(new Student("2026001", "Estudante Exemplo",
                        "estudante1@example.org", 1, course));
            }
        };
    }
}
