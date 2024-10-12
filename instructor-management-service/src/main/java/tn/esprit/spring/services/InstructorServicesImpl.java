package tn.esprit.spring.services;

import lombok.AllArgsConstructor;
import lombok.extern.flogger.Flogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import tn.esprit.spring.dto.CourseDTO;
import tn.esprit.spring.entities.Instructor;
import tn.esprit.spring.repositories.IInstructorRepository;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Service
@Slf4j
public class InstructorServicesImpl implements IInstructorServices {

    private IInstructorRepository instructorRepository;
    @Autowired
    private WebClient.Builder webClientBuilder;

    private final String COURSE_SERVICE_URL = "lb://course-management-service/course";  // URL of Course Service

    @Override
    public Instructor addInstructor(Instructor instructor) {
        return instructorRepository.save(instructor);
    }

    @Override
    public List<Instructor> retrieveAllInstructors() {
        return instructorRepository.findAll();
    }

    @Override
    public Instructor updateInstructor(Instructor instructor) {
        return instructorRepository.save(instructor);
    }

    @Override
    public Instructor retrieveInstructor(Long numInstructor) {
        return instructorRepository.findById(numInstructor).orElse(null);
    }

    @Override
    public Instructor addInstructorAndAssignToCourse(Instructor instructor, Long numCourse) {
        // Call Course Service using WebClient to get course details
        CourseDTO course = webClientBuilder
                .build()
                .get()
                .uri(COURSE_SERVICE_URL + "/get/" + numCourse)
                .retrieve()
                .bodyToMono(CourseDTO.class)
                .block(); // blocking for simplicity, consider non-blocking in production

        if (course == null) {
            log.error("Course not found for ID: {}", numCourse);
            return instructor;
        } else {
            log.info("Course retrieved: {}", course.toString());
        }

        // Add course ID to instructor if course is valid
        Set<Long> courseIds = instructor.getCourseIds() != null ? instructor.getCourseIds() : new HashSet<>();
        courseIds.add(numCourse);
        instructor.setCourseIds(courseIds);

        return instructorRepository.save(instructor);
    }
}
