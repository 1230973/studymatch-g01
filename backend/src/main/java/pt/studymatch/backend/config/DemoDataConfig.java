
package pt.studymatch.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import pt.studymatch.backend.model.CourseUnit;
import pt.studymatch.backend.model.DegreeCourse;
import pt.studymatch.backend.model.Student;

import pt.studymatch.backend.repository.CourseUnitRepository;
import pt.studymatch.backend.repository.DegreeCourseRepository;
import pt.studymatch.backend.repository.StudentRepository;

@Configuration
@Profile("demo")
public class DemoDataConfig {

    @Bean
    CommandLineRunner demoData(
            DegreeCourseRepository courses,
            StudentRepository students,
            CourseUnitRepository courseUnits) {

        return args -> {

            // Criar o curso de Engenharia Informatica
            DegreeCourse course = courses.findByCodeIgnoreCase("EI")
                    .orElseGet(() -> courses.save(
                            new DegreeCourse(
                                    "EI",
                                    "Engenharia Informatica",
                                    180
                            )
                    ));

            // Criar um estudante de exemplo
            if (!students.existsByStudentNumberIgnoreCase("2026001")) {
                students.save(new Student(
                        "2026001",
                        "Estudante Exemplo",
                        "estudante1@example.org",
                        1,
                        course
                ));
            }

            // Criar a unidade curricular Programacao
            if (!courseUnits.existsByCode("PROG")) {
                courseUnits.save(new CourseUnit(
                        "PROG",
                        "Programacao"
                ));
            }

            // Criar a unidade curricular Bases de Dados
            if (!courseUnits.existsByCode("BD")) {
                courseUnits.save(new CourseUnit(
                        "BD",
                        "Bases de Dados"
                ));
            }

            // Criar a unidade curricular Engenharia de Software
            if (!courseUnits.existsByCode("ES")) {
                courseUnits.save(new CourseUnit(
                        "ES",
                        "Engenharia de Software"
                ));
            }
        };
    }
}
