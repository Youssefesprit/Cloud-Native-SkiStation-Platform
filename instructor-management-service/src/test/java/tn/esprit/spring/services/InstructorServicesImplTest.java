package tn.esprit.spring.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tn.esprit.spring.dto.CourseDTO;
import tn.esprit.spring.entities.Instructor;
import tn.esprit.spring.repositories.IInstructorRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class InstructorServicesImplTest {

    @Mock
    private IInstructorRepository instructorRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private InstructorServicesImpl instructorServices;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Safe to remove
    }


    @Test
    void testAddInstructor() {
        Instructor instructor = new Instructor();
        when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

        Instructor result = instructorServices.addInstructor(instructor);

        assertNotNull(result);
        verify(instructorRepository, times(1)).save(instructor);
    }

    @Test
    void testRetrieveAllInstructors() {
        Instructor instructor1 = new Instructor();
        Instructor instructor2 = new Instructor();
        when(instructorRepository.findAll()).thenReturn(Arrays.asList(instructor1, instructor2));

        List<Instructor> result = instructorServices.retrieveAllInstructors();

        assertEquals(2, result.size());
        verify(instructorRepository, times(1)).findAll();
    }

    @Test
    void testUpdateInstructor() {
        Instructor instructor = new Instructor();
        when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

        Instructor result = instructorServices.updateInstructor(instructor);

        assertNotNull(result);
        verify(instructorRepository, times(1)).save(instructor);
    }

    @Test
    void testRetrieveInstructor() {
        Instructor instructor = new Instructor();
        when(instructorRepository.findById(anyLong())).thenReturn(Optional.of(instructor));

        Instructor result = instructorServices.retrieveInstructor(1L);

        assertNotNull(result);
        verify(instructorRepository, times(1)).findById(1L);
    }

    @Test
    void testAddInstructorAndAssignToCourse() {
        Instructor instructor = new Instructor();
        CourseDTO courseDTO = new CourseDTO();
        WebClient webClient = mock(WebClient.class);
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CourseDTO.class)).thenReturn(Mono.just(courseDTO));
        when(instructorRepository.save(any(Instructor.class))).thenReturn(instructor);

        Instructor result = instructorServices.addInstructorAndAssignToCourse(instructor, 1L);

        assertNotNull(result);
        verify(instructorRepository, times(1)).save(instructor);
        verify(webClientBuilder, times(1)).build();
        verify(webClient, times(1)).get();
        verify(requestHeadersUriSpec, times(1)).uri(anyString());
        verify(requestHeadersSpec, times(1)).retrieve();
        verify(responseSpec, times(1)).bodyToMono(CourseDTO.class);
    }

    @Test
    void testAddInstructorAndAssignToCourse_ExceptionHandling() {
        Instructor instructor = new Instructor();
        when(webClientBuilder.build()).thenThrow(new RuntimeException("WebClient error"));

        assertThrows(RuntimeException.class, () -> {
            instructorServices.addInstructorAndAssignToCourse(instructor, 1L);
        });

        verify(instructorRepository, never()).save(any(Instructor.class));
    }

    @Test
    void testRetrieveAllInstructors_EmptyResult() {
        when(instructorRepository.findAll()).thenReturn(Collections.emptyList());

        List<Instructor> result = instructorServices.retrieveAllInstructors();

        assertTrue(result.isEmpty());
        verify(instructorRepository, times(1)).findAll();
    }
}