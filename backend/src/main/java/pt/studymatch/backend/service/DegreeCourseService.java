package pt.studymatch.backend.service;

import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.studymatch.backend.dto.CreateDegreeCourseRequest;
import pt.studymatch.backend.dto.DegreeCourseResponse;
import pt.studymatch.backend.exception.DuplicateResourceException;
import pt.studymatch.backend.model.DegreeCourse;
import pt.studymatch.backend.repository.DegreeCourseRepository;

@Service
public class DegreeCourseService {
    private final DegreeCourseRepository repository;

    public DegreeCourseService(DegreeCourseRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<DegreeCourseResponse> list() {
        return repository.findAllByOrderByCodeAsc().stream().map(this::toResponse).toList();
    }

    @Transactional
    public DegreeCourseResponse create(CreateDegreeCourseRequest request) {
        String code = request.code().trim().toUpperCase(Locale.ROOT);
        if (repository.existsByCodeIgnoreCase(code)) {
            throw new DuplicateResourceException("Ja existe um curso com esse codigo.");
        }
        DegreeCourse course = repository.save(new DegreeCourse(
                code, request.name().trim(), request.totalCredits()));
        return toResponse(course);
    }

    private DegreeCourseResponse toResponse(DegreeCourse course) {
        return new DegreeCourseResponse(course.getCode(), course.getName(), course.getTotalCredits());
    }
}
