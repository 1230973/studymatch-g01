package pt.studymatch.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateStudentRequest(
        @NotBlank @Size(max = 30) String studentNumber,
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Email @Size(max = 200) String institutionalEmail,
        @NotNull @Min(1) @Max(10) Integer enrolledYear,
        @NotBlank String degreeCourseCode
) { }
