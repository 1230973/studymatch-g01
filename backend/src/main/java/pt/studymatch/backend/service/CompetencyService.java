
package pt.studymatch.backend.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.studymatch.backend.dto.CompetencyResponse;
import pt.studymatch.backend.model.Competency;
import pt.studymatch.backend.repository.CompetencyRepository;

@Service
public class CompetencyService {

    private final CompetencyRepository competencyRepository;

    public CompetencyService(CompetencyRepository competencyRepository) {
        this.competencyRepository = competencyRepository;
    }

    @Transactional(readOnly = true)
    public List<CompetencyResponse> getAllCompetencies() {

        return competencyRepository.findAll(Sort.by("code"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private CompetencyResponse toResponse(Competency competency) {

        return new CompetencyResponse(
                competency.getId(),
                competency.getCode(),
                competency.getName()
        );
    }
}
