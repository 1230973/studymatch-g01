
package pt.studymatch.backend.dto;

import java.time.Instant;

public record GroupGenerationSummaryResponse(
        Long id,
        Long contextId,
        Instant generatedAt,
        String algorithmVersion,
        String strategy,
        Integer groupCount
) {
}
