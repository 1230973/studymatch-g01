
package pt.studymatch.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import pt.studymatch.backend.model.Competency;
import pt.studymatch.backend.model.CourseUnit;
import pt.studymatch.backend.model.DegreeCourse;
import pt.studymatch.backend.model.Student;

import pt.studymatch.backend.repository.CompetencyRepository;
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
            CourseUnitRepository courseUnits,
            CompetencyRepository competencies) {

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

            // Criar as competencias
            Competency programming = competencies.findByCode("PROGRAMMING")
                    .orElseGet(() -> competencies.save(
                            new Competency("PROGRAMMING", "Programacao")
                    ));

            Competency databases = competencies.findByCode("DATABASES")
                    .orElseGet(() -> competencies.save(
                            new Competency("DATABASES", "Bases de Dados")
                    ));

            Competency softwareEngineering = competencies.findByCode("SOFTWARE_ENGINEERING")
                    .orElseGet(() -> competencies.save(
                            new Competency("SOFTWARE_ENGINEERING", "Engenharia de Software")
                    ));

            // Criar ou atualizar a unidade curricular Programacao
            CourseUnit prog = courseUnits.findByCode("PROG")
                    .orElseGet(() -> courseUnits.save(
                            new CourseUnit("PROG", "Programacao")
                    ));

            prog.setCompetency(programming);
            courseUnits.save(prog);

            // Criar ou atualizar a unidade curricular Bases de Dados
            CourseUnit bd = courseUnits.findByCode("BD")
                    .orElseGet(() -> courseUnits.save(
                            new CourseUnit("BD", "Bases de Dados")
                    ));

            bd.setCompetency(databases);
            courseUnits.save(bd);

            // Criar ou atualizar a unidade curricular Engenharia de Software
            CourseUnit es = courseUnits.findByCode("ES")
                    .orElseGet(() -> courseUnits.save(
                            new CourseUnit("ES", "Engenharia de Software")
                    ));

            es.setCompetency(softwareEngineering);
            courseUnits.save(es);
        };
    }
}
