
package pt.studymatch.backend.dto;

public record CompetencyScoreResponse(
        String code,
        String name,
        Double score,
        int evidenceCount
) {
}
