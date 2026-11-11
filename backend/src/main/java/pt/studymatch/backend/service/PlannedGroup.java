
package pt.studymatch.backend.service;

import java.util.List;

public record PlannedGroup(
        List<String> studentNumbers,
        Double balanceScore,
        String explanation
) {
}
