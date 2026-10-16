
package pt.studymatch.backend.dto;

import java.util.List;

public record StudentProfileResponse(
        String studentNumber,
        List<CompetencyScoreResponse> competencies
) {
}
