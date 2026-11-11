
package pt.studymatch.backend.dto;

import java.time.Instant;
import java.util.List;

public record GroupGenerationResponse(
        Long id,
        Long contextId,
        Instant generatedAt,
        String algorithmVersion,
        String strategy,
        String parameters,
        List<StudentGroupResponse> groups
) {
}

