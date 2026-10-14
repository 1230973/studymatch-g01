
package pt.studymatch.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pt.studymatch.backend.dto.CourseUnitResponse;
import pt.studymatch.backend.service.CourseUnitService;

@RestController
@RequestMapping("/api/course-units")
public class CourseUnitController {

    private final CourseUnitService courseUnitService;

    public CourseUnitController(CourseUnitService courseUnitService) {
        this.courseUnitService = courseUnitService;
    }

    @GetMapping
    public List<CourseUnitResponse> getAllCourseUnits() {
        return courseUnitService.getAllCourseUnits();
    }
}
