
package pt.studymatch.backend;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class GroupFormationContextApiTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void createsAndListsContexts() throws Exception {

        mvc.perform(post("/api/grouping-contexts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "Trabalho de Programacao",
                          "description": "Projeto do Sprint 3"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name")
                        .value("Trabalho de Programacao"));

        mvc.perform(get("/api/grouping-contexts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name")
                        .value("Trabalho de Programacao"));
    }

    @Test
    void rejectsContextWithoutName() throws Exception {

        mvc.perform(post("/api/grouping-contexts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "",
                          "description": "Sem nome"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.name").exists());
    }
}
