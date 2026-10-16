
package pt.studymatch.backend.dto;

public record ColdStartAnswerResponse(
        Long id,
        String competencyCode,
        Integer rating,
        Integer initialScore
) {
}
