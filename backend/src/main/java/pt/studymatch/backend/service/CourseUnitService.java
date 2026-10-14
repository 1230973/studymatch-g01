
package pt.studymatch.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import pt.studymatch.backend.dto.CourseUnitResponse;
import pt.studymatch.backend.model.CourseUnit;
import pt.studymatch.backend.repository.CourseUnitRepository;

@Service
public class CourseUnitService {

    private final CourseUnitRepository courseUnitRepository;

    public CourseUnitService(CourseUnitRepository courseUnitRepository) {
        this.courseUnitRepository = courseUnitRepository;
    }

    public List<CourseUnitResponse> getAllCourseUnits() {

        List<CourseUnit> courseUnits = courseUnitRepository.findAll();

        return courseUnits.stream()
                .map(courseUnit -> new CourseUnitResponse(
                        courseUnit.getId(),
                        courseUnit.getCode(),
                        courseUnit.getName()
                ))
                .toList();
    }
}
