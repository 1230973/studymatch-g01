
package pt.studymatch.backend.dto;

import java.util.List;

public record StudentGroupResponse(
        Long id,
        Integer groupNumber,
        Double balanceScore,
        String explanation,
        List<GroupMemberResponse> members
) {
}
