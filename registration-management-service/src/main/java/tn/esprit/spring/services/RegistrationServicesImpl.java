package tn.esprit.spring.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.dto.CourseDTO;
import tn.esprit.spring.dto.SkierDTO;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.IRegistrationRepository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class RegistrationServicesImpl implements IRegistrationServices {

    private IRegistrationRepository registrationRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    private final String SKIER_SERVICE_URL = "lb://skier-management-service/skier/";
    private final String COURSE_SERVICE_URL = "lb://course-management-service/course/";

    @Override
    public Registration addRegistrationAndAssignToSkier(Registration registration, Long numSkier) {
        SkierDTO skier = webClientBuilder
                .build()
                .get()
                .uri(SKIER_SERVICE_URL + "/get/" +numSkier)
                .retrieve()
                .bodyToMono(SkierDTO.class)
                .block(); // blocking for simplicity
        if (skier != null) {
            registration.setNumSkier(skier.getNumSkier());
        }

        return registrationRepository.save(registration);
    }

    @Override
    public Registration assignRegistrationToCourse(Long numRegistration, Long numCourse) {
        Registration registration = registrationRepository.findById(numRegistration).orElse(null);

        CourseDTO course = webClientBuilder
                .build()
                .get()
                .uri(COURSE_SERVICE_URL + "/get/" + numCourse)
                .retrieve()
                .bodyToMono(CourseDTO.class)
                .block(); // blocking for simplicity

        if (course == null) {
            log.error("Course with ID " + numCourse + " not found.");
            throw new IllegalArgumentException("Course not found");
        }
        if (registration == null) {
            log.error("Registration not found");
            throw new IllegalArgumentException("Registration not found");
        }

        if (registration != null && course != null) {
            registration.setNumCourse(course.getNumCourse());
        }
        return registrationRepository.save(registration);
    }

    @Transactional
    @Override
    public Registration addRegistrationAndAssignToSkierAndCourse(Registration registration, Long numSkieur, Long numCours) {
        SkierDTO skier = webClientBuilder
                .build()
                .get()
                .uri(SKIER_SERVICE_URL +"/get/" +numSkieur)
                .retrieve()
                .bodyToMono(SkierDTO.class)
                .block(); // blocking for simplicity

        CourseDTO course = webClientBuilder
                .build()
                .get()
                .uri(COURSE_SERVICE_URL +"/get/" +numCours)
                .retrieve()
                .bodyToMono(CourseDTO.class)
                .block(); // blocking for simplicity

        if (skier == null || course == null) {
            return null;
        }

        if (registrationRepository.countDistinctByNumWeekAndSkier_NumSkierAndCourse_NumCourse(
                registration.getNumWeek(), skier.getNumSkier(), course.getNumCourse()) >= 1) {
            log.info("Sorry, you're already registered for this course in week: " + registration.getNumWeek());
            return null;
        }

        int ageSkieur = Period.between(skier.getDateOfBirth(), LocalDate.now()).getYears();
        log.info("Age: " + ageSkieur);

        switch (course.getTypeCourse()) {
            case INDIVIDUAL:
                log.info("Adding without tests");
                return assignRegistration(registration, skier, course);

            case COLLECTIVE_CHILDREN:
                if (ageSkieur < 16) {
                    log.info("Ok CHILD!");
                    if (registrationRepository.countByNumCourseAndNumWeek(course.getNumCourse(), registration.getNumWeek()) < 6) {
                        log.info("Course successfully added!");
                        return assignRegistration(registration, skier, course);
                    } else {
                        log.info("Full Course! Please choose another week to register.");
                        return null;
                    }
                } else {
                    log.info("Sorry, your age doesn't allow you to register for this course! Try to register for a Collective Adult Course...");
                }
                break;

            default:
                if (ageSkieur >= 16) {
                    log.info("Ok ADULT!");
                    if (registrationRepository.countByNumCourseAndNumWeek(course.getNumCourse(), registration.getNumWeek()) < 6) {
                        log.info("Course successfully added!");
                        return assignRegistration(registration, skier, course);
                    } else {
                        log.info("Full Course! Please choose another week to register.");
                        return null;
                    }
                }
                log.info("Sorry, your age doesn't allow you to register for this course! Try to register for a Collective Child Course...");
        }
        return registration;
    }

    @Override
    public Registration retrieveRegistration(Long numRegistration) {
        return registrationRepository.findById(numRegistration).orElse(null);
    }

    private Registration assignRegistration(Registration registration, SkierDTO skier, CourseDTO course) {
        registration.setNumSkier(skier.getNumSkier());
        registration.setNumCourse(course.getNumCourse());
        return registrationRepository.save(registration);
    }

}
