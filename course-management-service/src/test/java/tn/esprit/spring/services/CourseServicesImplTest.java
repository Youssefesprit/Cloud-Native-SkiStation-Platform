package tn.esprit.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.TypeCourse;
import tn.esprit.spring.repositories.ICourseRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class CourseServicesImplTest {
    @Mock
    private ICourseRepository courseRepository;

    @InjectMocks
    private CourseServicesImpl courseServices;

    private Course course;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        course = new Course();
    }

    @Test
    void retrieveAllCourses() {
        List<Course> courseList = Arrays.asList(course);
        when(courseRepository.findAll()).thenReturn(courseList);

        List<Course> result = courseServices.retrieveAllCourses();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void addCourse() {
        when(courseRepository.save(course)).thenReturn(course);

        Course result = courseServices.addCourse(course);
        assertNotNull(result);
        assertEquals(course, result);
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    void updateCourse() {
        when(courseRepository.save(course)).thenReturn(course);

        Course result = courseServices.updateCourse(course);
        assertNotNull(result);
        assertEquals(course, result);
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    void retrieveCourse() {
        Long numCourse = 1L;
        when(courseRepository.findById(numCourse)).thenReturn(Optional.of(course));

        Course result = courseServices.retrieveCourse(numCourse);
        assertNotNull(result);
        assertEquals(course, result);
        verify(courseRepository, times(1)).findById(numCourse);
    }

    @Test
    void retrieveCoursesByType() {
        TypeCourse type = TypeCourse.COLLECTIVE_ADULT; //collectiveadult mel enum TypeCourse
        List<Course> courseList = Arrays.asList(course);
        when(courseRepository.findByType(type)).thenReturn(courseList);

        List<Course> result = courseServices.retrieveCoursesByType(type);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(courseList, result);
        verify(courseRepository, times(1)).findByType(type);
    }

}