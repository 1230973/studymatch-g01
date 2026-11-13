package pt.studymatch.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import pt.studymatch.backend.model.Competency;
import pt.studymatch.backend.model.CourseUnit;
import pt.studymatch.backend.model.DegreeCourse;
import pt.studymatch.backend.model.Student;
import pt.studymatch.backend.model.UserAccount;
import pt.studymatch.backend.repository.CompetencyRepository;
import pt.studymatch.backend.repository.CourseUnitRepository;
import pt.studymatch.backend.repository.DegreeCourseRepository;
import pt.studymatch.backend.repository.StudentRepository;
import pt.studymatch.backend.repository.UserAccountRepository;

@Configuration
@Profile("demo")
public class DemoDataConfig {

    @Bean
    CommandLineRunner demoData(
            DegreeCourseRepository courses,
            StudentRepository students,
            CourseUnitRepository courseUnits,
            CompetencyRepository competencies,
            UserAccountRepository userAccounts,
            PasswordEncoder passwordEncoder) {

        return args -> {

            DegreeCourse course = courses.findByCodeIgnoreCase("EI")
                    .orElseGet(() -> courses.save(
                            new DegreeCourse(
                                    "EI",
                                    "Engenharia Informatica",
                                    180
                            )
                    ));

            Student s1 = students.findByStudentNumberIgnoreCase("2026001")
                    .orElseGet(() -> students.save(new Student(
                            "2026001",
                            "Estudante Exemplo",
                            "estudante1@example.org",
                            1,
                            course
                    )));

            Student s2 = students.findByStudentNumberIgnoreCase("2026002")
                    .orElseGet(() -> students.save(new Student(
                            "2026002",
                            "Maria Caloira (Cold Start)",
                            "maria2026002@example.org",
                            1,
                            course
                    )));

            // Conta de Professor: professor / prof123
            if (!userAccounts.existsByUsernameIgnoreCase("professor")) {
                userAccounts.save(new UserAccount("professor", passwordEncoder.encode("prof123"), "ROLE_TEACHER", null));
            }
            if (!userAccounts.existsByUsernameIgnoreCase("2026001")) {
                userAccounts.save(new UserAccount("2026001", passwordEncoder.encode("aluno123"), "ROLE_STUDENT", s1));
            }
            if (!userAccounts.existsByUsernameIgnoreCase("2026002")) {
                userAccounts.save(new UserAccount("2026002", passwordEncoder.encode("aluno123"), "ROLE_STUDENT", s2));
            }

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

            CourseUnit prog = courseUnits.findByCode("PROG")
                    .orElseGet(() -> courseUnits.save(
                            new CourseUnit("PROG", "Programacao")
                    ));
            prog.setCompetency(programming);
            courseUnits.save(prog);

            CourseUnit bd = courseUnits.findByCode("BD")
                    .orElseGet(() -> courseUnits.save(
                            new CourseUnit("BD", "Bases de Dados")
                    ));
            bd.setCompetency(databases);
            courseUnits.save(bd);

            CourseUnit es = courseUnits.findByCode("ES")
                    .orElseGet(() -> courseUnits.save(
                            new CourseUnit("ES", "Engenharia de Software")
                    ));
            es.setCompetency(softwareEngineering);
            courseUnits.save(es);
        };
    }
}