package pt.studymatch.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import pt.studymatch.backend.model.UserAccount;
import pt.studymatch.backend.repository.DegreeCourseRepository;
import pt.studymatch.backend.repository.StudentRepository;
import pt.studymatch.backend.repository.UserAccountRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "professor", roles = "TEACHER")
class StudyMatchApiIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired StudentRepository students;
    @Autowired DegreeCourseRepository courses;
    @Autowired UserAccountRepository userAccounts;
    @Autowired PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDatabase() {
        userAccounts.deleteAll();
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

    @Test
    @WithAnonymousUser
    void rejectsUnauthenticatedAccessToProtectedEndpoints() throws Exception {
        mvc.perform(get("/api/students"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "2026001", roles = "STUDENT")
    void forbidsStudentFromListingAllStudentsOrCreatingCourse() throws Exception {
        mvc.perform(get("/api/students"))
                .andExpect(status().isForbidden());

        mvc.perform(post("/api/degree-courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"EI\",\"name\":\"Engenharia Informatica\",\"totalCredits\":180}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "2026001", roles = "STUDENT")
    void forbidsStudentFromAccessingAnotherStudentProfile() throws Exception {
        mvc.perform(get("/api/students/2026002/profile"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void loginMaintainsSessionAndLogoutInvalidatesIt() throws Exception {
        userAccounts.save(new UserAccount(
                "professor",
                passwordEncoder.encode("prof123"),
                "ROLE_TEACHER",
                null
        ));

        MvcResult loginResult = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"professor\",\"password\":\"prof123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Docente"))
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
        org.springframework.security.test.context.TestSecurityContextHolder.clearContext();

        mvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("professor"));

        mvc.perform(post("/api/auth/logout").session(session))
                .andExpect(status().isOk());

        mvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isUnauthorized());
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