
package pt.studymatch.backend.controller;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import pt.studymatch.backend.dto.CreateGroupFormationContextRequest;
import pt.studymatch.backend.dto.GroupFormationContextResponse;
import pt.studymatch.backend.service.GroupFormationContextService;

@RestController
@RequestMapping("/api/grouping-contexts")
public class GroupFormationContextController {

    private final GroupFormationContextService service;

    public GroupFormationContextController(
            GroupFormationContextService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<GroupFormationContextResponse> create(
            @Valid @RequestBody
            CreateGroupFormationContextRequest request) {

        GroupFormationContextResponse context =
                service.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(context.id())
                .toUri();

        return ResponseEntity.created(location).body(context);
    }

    @GetMapping
    public List<GroupFormationContextResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public GroupFormationContextResponse find(
            @PathVariable Long id) {
        return service.find(id);
    }
}
