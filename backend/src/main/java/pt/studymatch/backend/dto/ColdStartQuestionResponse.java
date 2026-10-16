
package pt.studymatch.backend.dto;

public record ColdStartQuestionResponse(
        String competencyCode,
        String question,
        int minRating,
        int maxRating
) {
}
