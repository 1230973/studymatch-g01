
package pt.studymatch.backend.dto;

public record CompetencyScoreResponse(
        String code,
        String name,
        Double score,
        int evidenceCount,
        String source
) {

    public CompetencyScoreResponse(
            String code,
            String name,
            Double score,
            int evidenceCount) {

        this(
                code,
                name,
                score,
                evidenceCount,
                score == null ? "UNKNOWN" : "ACADEMIC"
        );
    }
}
