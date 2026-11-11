
package pt.studymatch.backend.dto;

import java.time.Instant;

public record GroupFormationContextResponse(

        Long id,
        String name,
        String description,
        Instant createdAt,
        String createdBy

) {
}
