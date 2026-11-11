
package pt.studymatch.backend.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.studymatch.backend.dto.CreateGroupFormationContextRequest;
import pt.studymatch.backend.dto.GroupFormationContextResponse;
import pt.studymatch.backend.exception.ResourceNotFoundException;
import pt.studymatch.backend.model.GroupFormationContext;
import pt.studymatch.backend.repository.GroupFormationContextRepository;

@Service
public class GroupFormationContextService {

    private final GroupFormationContextRepository repository;

    public GroupFormationContextService(
            GroupFormationContextRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public GroupFormationContextResponse create(
            CreateGroupFormationContextRequest request) {

        String name = request.name().trim();

        String description = request.description() == null
                ? null
                : request.description().trim();

        // A autoria sera associada ao professor autenticado
        // quando o sistema de login estiver integrado.
        GroupFormationContext context =
                new GroupFormationContext(name, description, null);

        GroupFormationContext saved = repository.save(context);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<GroupFormationContextResponse> list() {

        return repository.findAll(
                Sort.by(Sort.Direction.DESC, "createdAt")
        ).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public GroupFormationContextResponse find(Long id) {

        GroupFormationContext context = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Contexto nao encontrado."));

        return toResponse(context);
    }

    private GroupFormationContextResponse toResponse(
            GroupFormationContext context) {

        return new GroupFormationContextResponse(
                context.getId(),
                context.getName(),
                context.getDescription(),
                context.getCreatedAt(),
                context.getCreatedBy()
        );
    }
}
