
package pt.studymatch.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pt.studymatch.backend.dto.GroupGenerationResponse;
import pt.studymatch.backend.dto.GroupGenerationSummaryResponse;
import pt.studymatch.backend.service.GroupGenerationService;

@RestController
@RequestMapping("/api/grouping-contexts/{contextId}/generations")
public class GroupGenerationController {

    private final GroupGenerationService service;

    public GroupGenerationController(
            GroupGenerationService service) {
        this.service = service;
    }

    @GetMapping
    public List<GroupGenerationSummaryResponse> list(
            @PathVariable Long contextId) {

        return service.list(contextId);
    }

    @GetMapping("/{generationId}")
    public GroupGenerationResponse find(
            @PathVariable Long contextId,
            @PathVariable Long generationId) {

        return service.find(contextId, generationId);
    }
}
