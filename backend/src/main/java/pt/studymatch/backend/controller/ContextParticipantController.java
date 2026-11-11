
package pt.studymatch.backend.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pt.studymatch.backend.dto.ContextParticipantResponse;
import pt.studymatch.backend.dto.UpdateContextParticipantsRequest;
import pt.studymatch.backend.service.ContextParticipantService;

@RestController
@RequestMapping("/api/grouping-contexts/{contextId}/participants")
public class ContextParticipantController {

    private final ContextParticipantService service;

    public ContextParticipantController(
            ContextParticipantService service) {
        this.service = service;
    }

    @PutMapping
    public List<ContextParticipantResponse> replace(
            @PathVariable Long contextId,
            @Valid @RequestBody
            UpdateContextParticipantsRequest request) {

        return service.replace(contextId, request);
    }

    @GetMapping
    public List<ContextParticipantResponse> list(
            @PathVariable Long contextId) {

        return service.list(contextId);
    }
}
