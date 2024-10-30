package tn.esprit.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tn.esprit.spring.dto.SubscriptionDTO;
import tn.esprit.spring.entities.Skier;
import tn.esprit.spring.repositories.ISkierRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class SkierServicesImplTest {
    @Mock
    private ISkierRepository skierRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private SkierServicesImpl skierServices;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }


    @Test
    public void addSkierWithInvalidSubscription() {
        Skier skier = new Skier();
        skier.setSubscriptionId(1L);
        when(responseSpec.bodyToMono(SubscriptionDTO.class)).thenReturn(Mono.empty());
        assertThrows(IllegalArgumentException.class, () -> skierServices.addSkier(skier));
    }

    @Test
    public void assignSkierToSubscriptionWithValidSkierAndSubscription() {
        Skier skier = new Skier();
        skier.setNumSkier(1L);
        SubscriptionDTO subscriptionDTO = new SubscriptionDTO();
        subscriptionDTO.setNumSub(1L);

        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(responseSpec.bodyToMono(SubscriptionDTO.class)).thenReturn(Mono.just(subscriptionDTO));
        when(skierRepository.save(skier)).thenReturn(skier);

        Skier result = skierServices.assignSkierToSubscription(1L, 1L);

        assertEquals(skier, result);
        verify(skierRepository, times(1)).save(skier);
    }

    @Test
    public void assignSkierToSubscriptionWithInvalidSkier() {
        when(skierRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> skierServices.assignSkierToSubscription(1L, 1L));
    }

    @Test
    public void assignSkierToSubscriptionWithInvalidSubscription() {
        Skier skier = new Skier();
        skier.setNumSkier(1L);

        when(skierRepository.findById(1L)).thenReturn(Optional.of(skier));
        when(responseSpec.bodyToMono(SubscriptionDTO.class)).thenReturn(Mono.empty());

        assertThrows(IllegalArgumentException.class, () -> skierServices.assignSkierToSubscription(1L, 1L));
    }

}