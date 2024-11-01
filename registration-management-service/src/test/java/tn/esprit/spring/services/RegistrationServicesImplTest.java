package tn.esprit.spring.services;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tn.esprit.spring.dto.CourseDTO;
import tn.esprit.spring.dto.SkierDTO;
import tn.esprit.spring.dto.TypeCourse;
import tn.esprit.spring.entities.Registration;
import tn.esprit.spring.repositories.IRegistrationRepository;
import java.time.LocalDate;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
public class RegistrationServicesImplTest {
    @InjectMocks
    private RegistrationServicesImpl registrationServices;

    @Mock
    private IRegistrationRepository registrationRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient.ResponseSpec responseSpec;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient webClient;


    private Registration registration;

    private final String SKIER_SERVICE_URL = "lb://skier-management-service/skier/";
    private final String COURSE_SERVICE_URL = "lb://course-management-service/course/";
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        registration = new Registration();
        registration.setNumSkier(1L);
        registration.setNumCourse(0L);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }
    /**
     * Test pour la méthode addRegistrationAndAssignToSkier
     */
    @Test
    public void testAddRegistrationAndAssignToSkier_Success() {
        // Arrange
        Long numSkier = 1L;
        SkierDTO skierDTO = new SkierDTO();
        skierDTO.setNumSkier(numSkier);

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(SkierDTO.class)).thenReturn(Mono.just(skierDTO));
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);


        Registration result = registrationServices.addRegistrationAndAssignToSkier(registration, numSkier);

        assertNotNull(result);
        assertEquals(numSkier, result.getNumSkier());
        verify(registrationRepository, times(1)).save(registration);
    }

    @Test
    public void testAddRegistrationAndAssignToSkier_SkierNotFound() {
        // Arrange
        Long numSkier = 1L;
        Registration registration = new Registration();

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(SkierDTO.class)).thenReturn(Mono.empty());

        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);

        Registration result = registrationServices.addRegistrationAndAssignToSkier(registration, numSkier);

        assertNotNull(result);
        assertNull(result.getNumSkier());
        verify(registrationRepository, times(1)).save(registration);
    }

    /**
     * Test pour la méthode assignRegistrationToCourse
     */
    @Test
    public void testAssignRegistrationToCourse() {
        // Arrange
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);

        SkierDTO mockedSkier = new SkierDTO();
        mockedSkier.setNumSkier(1L);
        when(webClientBuilder.build()
                .get()
                .uri(SKIER_SERVICE_URL + "/get/" + 1L)
                .retrieve()
                .bodyToMono(SkierDTO.class))
                .thenReturn(Mono.just(mockedSkier));

        CourseDTO mockedCourse = new CourseDTO();
        mockedCourse.setNumCourse(1L);
        when(webClientBuilder.build()
                .get()
                .uri(COURSE_SERVICE_URL + "/get/" + 1L)
                .retrieve()
                .bodyToMono(CourseDTO.class))
                .thenReturn(Mono.just(mockedCourse));


        Registration result = registrationServices.assignRegistrationToCourse(1L, 1L);

        assertEquals(1L, result.getNumCourse()); // Vérifie que le numéro du cours est bien assigné
        Mockito.verify(registrationRepository, Mockito.times(1)).save(any(Registration.class)); // Vérifie que save a été appelé une fois
    }


    /**
     * Test pour la méthode addRegistrationAndAssignToSkierAndCourse
     */
    @Test
    public void testAddRegistrationAndAssignToSkierAndCourse() {
        // Arrange
        SkierDTO mockedSkier = new SkierDTO();
        mockedSkier.setNumSkier(1L);
        mockedSkier.setDateOfBirth(LocalDate.now().minusYears(10)); // par exemple, 10 ans

        CourseDTO mockedCourse = new CourseDTO();
        mockedCourse.setNumCourse(1L);
        mockedCourse.setTypeCourse(TypeCourse.COLLECTIVE_CHILDREN); // Exemple

        when(registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(any(Integer.class), any(Long.class), any(Long.class))).thenReturn(0L);
        when(webClientBuilder.build().get().uri(SKIER_SERVICE_URL + "/get/1").retrieve().bodyToMono(SkierDTO.class)).thenReturn(Mono.just(mockedSkier));
        when(webClientBuilder.build().get().uri(COURSE_SERVICE_URL + "/get/1").retrieve().bodyToMono(CourseDTO.class)).thenReturn(Mono.just(mockedCourse));
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);

        // Act
        Registration result = registrationServices.addRegistrationAndAssignToSkierAndCourse(registration, 1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getNumSkier());
        assertEquals(1L, result.getNumCourse());
        Mockito.verify(registrationRepository, Mockito.times(1)).save(any(Registration.class));
    }

    /**
     * Test pour la méthode retrieveRegistration
     */
    @Test
    public void testRetrieveRegistration() {
        // Arrange
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));

        // Act
        Registration result = registrationServices.retrieveRegistration(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getNumSkier());
        Mockito.verify(registrationRepository, Mockito.times(1)).findById(1L);
    }

    /**
     * Test pour la méthode assignRegistrationToCourse avec une exception pour un cours non trouvé
     */
    @Test
    public void testAssignRegistrationToCourse_CourseNotFound() {
        // Arrange
        when(registrationRepository.findById(1L)).thenReturn(Optional.of(registration));
        when(responseSpec.bodyToMono(CourseDTO.class)).thenReturn(Mono.empty()); // Simule un cours non trouvé

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            registrationServices.assignRegistrationToCourse(1L, 999L);
        });

        // Vous pouvez également ajouter un message à vérifier si besoin
        assertEquals("Course not found", thrown.getMessage());
    }




    @Test
    public void testAssignRegistrationToCourse_RegistrationNotFound() {
        // Arrange
        when(registrationRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            registrationServices.assignRegistrationToCourse(1L, 1L);
        });

        assertEquals("Registration not found", thrown.getMessage());
    }

    /**
     * Test pour la méthode assignRegistrationToCourse avec une exception pour une inscription non trouvée
     */
   /* @Test
    public void testAssignRegistrationToCourse_RegistrationNotFound() {
        // Arrange
        when(registrationRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            registrationServices.assignRegistrationToCourse(999L, 1L);
        });
    }*/
}
