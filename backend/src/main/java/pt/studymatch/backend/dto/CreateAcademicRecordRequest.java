
package pt.studymatch.backend.dto;

import java.math.BigDecimal;

public record CreateAcademicRecordRequest(
        String courseUnitCode,
        BigDecimal grade,
        String academicYear,
        Integer semester
) {
}
