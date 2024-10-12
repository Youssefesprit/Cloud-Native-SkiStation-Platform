package tn.esprit.spring.services;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.dto.*;
import tn.esprit.spring.entities.Skier;
import tn.esprit.spring.repositories.ISkierRepository;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SkierServicesImpl implements ISkierServices {

    private ISkierRepository skierRepository;

    // WebClient for microservice communication
    @Autowired
    private WebClient.Builder webClientBuilder;

    // URLs for external microservices
    private final String SUBSCRIPTION_SERVICE_URL = "lb://subscription-management-service/subscription/";
    private final String COURSE_SERVICE_URL = "lb://course-management-service/course/";
    private final String PISTE_SERVICE_URL = "lb://piste-management-service/piste/";
    private final String REGISTRATION_SERVICE_URL = "lb://registration-management-service/registration/";

    @Override
    public List<Skier> retrieveAllSkiers() {
        return skierRepository.findAll();
    }

    @Override
    public Skier addSkier(Skier skier) {
        // Set subscriptionId based on the received subscription
        Long subscriptionId = skier.getSubscriptionId();

        // Fetch Subscription from Subscription microservice to validate or calculate based on type
        SubscriptionDTO subscriptionDTO = webClientBuilder
                .build()
                .get()
                .uri(SUBSCRIPTION_SERVICE_URL +"/get/" +subscriptionId )
                .retrieve()
                .bodyToMono(SubscriptionDTO.class)
                .block();

        if (subscriptionDTO != null) {
            //  i thik you need to update the Subscirption here so
            switch (subscriptionDTO.getTypeSub()) {
                case ANNUAL:
                    subscriptionDTO.setEndDate(subscriptionDTO.getStartDate().plusYears(1));
                    break;
                case SEMESTRIEL:
                    subscriptionDTO.setEndDate(subscriptionDTO.getStartDate().plusMonths(6));
                    break;
                case MONTHLY:
                    subscriptionDTO.setEndDate(subscriptionDTO.getStartDate().plusMonths(1));
                    break;
            }

            skier.setSubscriptionId(subscriptionDTO.getNumSub());


            return skierRepository.save(skier);
        } else {
            // Handle the case where the subscription is not valid or not found
            throw new IllegalArgumentException("Invalid subscription ID: " + subscriptionId);
        }
    }
    @Override
    public Skier assignSkierToSubscription(Long numSkier, Long numSubscription) {
        Skier skier = skierRepository.findById(numSkier).orElse(null);

        if (skier != null) {
            // Fetch Subscription from Subscription microservice to check if it exists
            SubscriptionDTO subscriptionDTO = webClientBuilder
                    .build()
                    .get()
                    .uri(SUBSCRIPTION_SERVICE_URL + "/get/" + numSubscription)
                    .retrieve()
                    .bodyToMono(SubscriptionDTO.class)
                    .block();

            // Check if subscription exists
            if (subscriptionDTO != null) {
                // Set subscription ID for the skier if subscription exists
                skier.setSubscriptionId(numSubscription);
                return skierRepository.save(skier);
            } else {
                throw new IllegalArgumentException("Subscription with ID " + numSubscription + " does not exist.");
            }
        } else {
            throw new IllegalArgumentException("Skier with ID " + numSkier + " does not exist.");
        }
    }

    @Override
    public Skier addSkierAndAssignToCourse(Skier skier, Long numCourse) {
        // Save the skier first
        Skier savedSkier = skierRepository.save(skier);

        // Fetch the course from the Course microservice
        CourseDTO courseDTO = webClientBuilder
                .build()
                .get()
                .uri(COURSE_SERVICE_URL + "/get/" + numCourse)  // Assuming /get/ endpoint for fetching course
                .retrieve()
                .bodyToMono(CourseDTO.class)
                .block();

        // Ensure the course exists before proceeding
        if (courseDTO == null) {
            throw new IllegalArgumentException("Course with ID " + numCourse + " does not exist.");
        }

        // Get the registration IDs from the saved skier
        Set<Long> registrationIds = savedSkier.getRegistrationIds();

        // Check if registrationIds are not null and not empty
        if (registrationIds != null && !registrationIds.isEmpty()) {
            for (Long registrationId : registrationIds) {
                // Fetch the existing registration to ensure numSkier is updated
                RegistrationDTO registrationDTO = webClientBuilder
                        .build()
                        .get()
                        .uri(REGISTRATION_SERVICE_URL + "/get/" + registrationId)
                        .retrieve()
                        .bodyToMono(RegistrationDTO.class)
                        .block();

                if (registrationDTO != null) {
                    // Update both Skier ID and Course ID in the registration
                    registrationDTO.setSkierId(savedSkier.getNumSkier());
                    registrationDTO.setCourseId(courseDTO.getNumCourse());

                    // Update Registration via Registration microservice
                    webClientBuilder
                            .build()
                            .put()
                            .uri(REGISTRATION_SERVICE_URL + "/addAndAssignToSkierAndCourse/"  + savedSkier.getNumSkier() + "/" + courseDTO.getNumCourse())
                            .bodyValue(registrationDTO)
                            .retrieve()
                            .bodyToMono(Void.class)
                            .block();  // Blocking for simplicity
                }
            }
        } else {
            // If no registrations are found, throw an exception or handle it accordingly
            throw new IllegalArgumentException("No registrations found for skier with ID " + savedSkier.getNumSkier());
        }

        return savedSkier;
    }


    @Override
    public void removeSkier(Long numSkier) {
        skierRepository.deleteById(numSkier);
    }

    @Override
    public Skier retrieveSkier(Long numSkier) {
        return skierRepository.findById(numSkier).orElse(null);
    }

    @Override
    public Skier assignSkierToPiste(Long numSkieur, Long numPiste) {
        Skier skier = skierRepository.findById(numSkieur).orElse(null);

        // Fetch Piste from Piste microservice
        PisteDTO pisteDTO = webClientBuilder
                .build()
                .get()
                .uri(PISTE_SERVICE_URL +"/get/" +numPiste)
                .retrieve()
                .bodyToMono(PisteDTO.class)
                .block();
        // Ensure the course exists before proceeding
        if (pisteDTO == null) {
            throw new IllegalArgumentException("Pist with ID " + numPiste + " does not exist.");
        }

        if (skier != null && pisteDTO != null) {
            Set<Long> pisteIds = skier.getPisteIds();
            if (pisteIds == null) {
                pisteIds = new HashSet<>();
            }
            pisteIds.add(pisteDTO.getNumPiste());
            skier.setPisteIds(pisteIds);
            return skierRepository.save(skier);
        }

        return null;
    }
    @Override
    public List<Skier> retrieveSkiersBySubscriptionType(TypeSubscription typeSubscription) {
        // Fetch all skiers
        List<Skier> allSkiers = skierRepository.findAll();

        // Filter based on subscription type by fetching subscription details from the SubscriptionService
        return allSkiers.stream()
                .filter(skier -> {
                    // Fetch subscription details from the SubscriptionService
                    SubscriptionDTO subscriptionDTO = webClientBuilder
                            .build()
                            .get()
                            .uri(SUBSCRIPTION_SERVICE_URL +" /get/" +skier.getSubscriptionId())
                            .retrieve()
                            .bodyToMono(SubscriptionDTO.class)
                            .block(); // Blocking for simplicity

                    return subscriptionDTO != null && subscriptionDTO.getTypeSub() == typeSubscription;
                })
                .collect(Collectors.toList());
    }
}
