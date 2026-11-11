
package pt.studymatch.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateGroupFormationContextRequest(

        @NotBlank(message = "O nome e obrigatorio.")
        @Size(max = 150)
        String name,

        @Size(max = 1000)
        String description

) {
}
