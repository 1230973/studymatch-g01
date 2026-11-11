
package pt.studymatch.backend.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateContextParticipantsRequest(

        @NotNull(message = "A lista de estudantes e obrigatoria.")
        List<@NotBlank(message = "O numero de estudante nao pode estar vazio.")
             String> studentNumbers

) {
}
