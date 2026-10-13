
package pt.studymatch.backend.dto;

import java.math.BigDecimal;

public record AcademicRecordResponse(
        Long id,
        String courseUnitCode,
        String courseUnitName,
        BigDecimal grade,
        String academicYear,
        Integer semester
) {
}
