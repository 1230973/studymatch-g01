package pt.studymatch.backend.dto;

public record StudentResponse(
        String studentNumber,
        String name,
        String institutionalEmail,
        Integer enrolledYear,
        String degreeCourseCode,
        String degreeCourseName
) { }
