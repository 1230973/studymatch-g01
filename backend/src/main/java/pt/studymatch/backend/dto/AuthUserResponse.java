package pt.studymatch.backend.dto;

public record AuthUserResponse(
        String username,
        String role,
        String studentNumber,
        String displayName
) {
}
