package pt.studymatch.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import pt.studymatch.backend.repository.DegreeCourseRepository;
import pt.studymatch.backend.repository.StudentRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StudyMatchApiIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired StudentRepository students;
    @Autowired DegreeCourseRepository courses;

    @BeforeEach
    void cleanDatabase() {
        students.deleteAll();
        courses.deleteAll();
    }

    @Test
    void healthChecksDatabase() throws Exception {
        mvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.database").value("UP"));
    }

    @Test
    void createsCourseAndStudentAndReadsFromPersistence() throws Exception {
        createCourse();
        String student = """
                {"studentNumber":"2026001","name":"Ana Exemplo",
                 "institutionalEmail":"ana@example.org","enrolledYear":1,"degreeCourseCode":"EI"}
                """;
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON).content(student))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.degreeCourseCode").value("EI"));
        mvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentNumber").value("2026001"))
                .andExpect(jsonPath("$[0].name").value("Ana Exemplo"));
        mvc.perform(get("/api/students/2026001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.institutionalEmail").value("ana@example.org"));
    }

    @Test
    void refusesDuplicateStudent() throws Exception {
        createCourse();
        String student = """
                {"studentNumber":"2026001","name":"Ana Exemplo",
                 "institutionalEmail":"ana@example.org","enrolledYear":1,"degreeCourseCode":"EI"}
                """;
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON).content(student))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON).content(student))
                .andExpect(status().isConflict());
    }

    @Test
    void validatesStudentDataAndMissingCourse() throws Exception {
        String invalid = """
                {"studentNumber":"2026001","name":"Ana Exemplo",
                 "institutionalEmail":"invalido","enrolledYear":0,"degreeCourseCode":"EI"}
                """;
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON).content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.enrolledYear").exists());
        String noCourse = """
                {"studentNumber":"2026001","name":"Ana Exemplo",
                 "institutionalEmail":"ana@example.org","enrolledYear":1,"degreeCourseCode":"NAO_EXISTE"}
                """;
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON).content(noCourse))
                .andExpect(status().isNotFound());
    }

    @Test
    void returns404ForUnknownStudent() throws Exception {
        mvc.perform(get("/api/students/NAO_EXISTE"))
                .andExpect(status().isNotFound());
    }

    private void createCourse() throws Exception {
        mvc.perform(post("/api/degree-courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code":"EI","name":"Engenharia Informatica","totalCredits":180}
                                """))
                .andExpect(status().isCreated());
    }
}
