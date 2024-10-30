package tn.esprit.spring;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;
import tn.esprit.spring.repositories.ISubscriptionRepository;
import tn.esprit.spring.services.SubscriptionServicesImpl;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
class SubsManagementTests {
    @InjectMocks
    private SubscriptionServicesImpl subscriptionService;
    @Mock
    private ISubscriptionRepository subscriptionRepository;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testAddSubscriptionAnnual() {
        Subscription subscription = new Subscription();
        subscription.setTypeSub(TypeSubscription.ANNUAL);
        subscription.setStartDate(LocalDate.now());
        when(subscriptionRepository.save(any())).thenReturn(subscription);
        Subscription result = subscriptionService.addSubscription(subscription);
        assertNotNull(result);
        assertEquals(subscription.getEndDate(), LocalDate.now().plusYears(1));
        verify(subscriptionRepository).save(subscription);
    }
    @Test
    void testAddSubscriptionSemestriel() {
        Subscription subscription = new Subscription();
        subscription.setTypeSub(TypeSubscription.SEMESTRIEL);
        subscription.setStartDate(LocalDate.now());
        when(subscriptionRepository.save(any())).thenReturn(subscription);
        Subscription result = subscriptionService.addSubscription(subscription);
        assertNotNull(result);
        assertEquals(subscription.getEndDate(), LocalDate.now().plusMonths(6));
        verify(subscriptionRepository).save(subscription);
    }
    @Test
    void testAddSubscriptionMonthly() {
        Subscription subscription = new Subscription();
        subscription.setTypeSub(TypeSubscription.MONTHLY);
        subscription.setStartDate(LocalDate.now());
        when(subscriptionRepository.save(any())).thenReturn(subscription);
        Subscription result = subscriptionService.addSubscription(subscription);
        assertNotNull(result);
        assertEquals(subscription.getEndDate(), LocalDate.now().plusMonths(1));
        verify(subscriptionRepository).save(subscription);
    }
    @Test
    void testUpdateSubscription() {
        Subscription subscription = new Subscription();
        subscription.setNumSub(1L);
        when(subscriptionRepository.save(any())).thenReturn(subscription);
        Subscription result = subscriptionService.updateSubscription(subscription);
        assertNotNull(result);
        verify(subscriptionRepository).save(subscription);
    }
    @Test
    void testRetrieveSubscriptionById() {
        Subscription subscription = new Subscription();
        subscription.setNumSub(1L);
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));
        Subscription result = subscriptionService.retrieveSubscriptionById(1L);
        assertNotNull(result);
        assertEquals(subscription.getNumSub(), result.getNumSub());
        verify(subscriptionRepository).findById(1L);
    }
    @Test
    void testRetrieveSubscriptionByIdNotFound() {
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.empty());
        Subscription result = subscriptionService.retrieveSubscriptionById(1L);
        assertNull(result);
        verify(subscriptionRepository).findById(1L);
    }
    @Test
    void testAddSubscriptionEndDateCalculationEdgeCase() {
        Subscription subscription = new Subscription();
        subscription.setTypeSub(TypeSubscription.ANNUAL);
        subscription.setStartDate(LocalDate.of(2024, 12, 31)); // Start at the end of the year
        when(subscriptionRepository.save(any())).thenReturn(subscription);
        Subscription result = subscriptionService.addSubscription(subscription);
        assertNotNull(result);
        assertEquals(LocalDate.of(2025, 12, 31), result.getEndDate());
        verify(subscriptionRepository).save(subscription);
    }
    @Test
    void testUpdateSubscriptionThrowsException() {
        Subscription subscription = new Subscription();
        subscription.setNumSub(1L);
        when(subscriptionRepository.save(any())).thenThrow(new RuntimeException("Database error"));
        Exception exception = assertThrows(RuntimeException.class, () -> {
            subscriptionService.updateSubscription(subscription);
        });
        assertEquals("Database error", exception.getMessage());
        verify(subscriptionRepository).save(subscription);
    }
    @Test
    void testRetrieveSubscriptionsByDates() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        Subscription sub1 = new Subscription();
        sub1.setStartDate(LocalDate.of(2024, 6, 1));
        sub1.setEndDate(LocalDate.of(2025, 6, 1));
        Subscription sub2 = new Subscription();
        sub2.setStartDate(LocalDate.of(2024, 11, 1));
        sub2.setEndDate(LocalDate.of(2025, 11, 1));
        when(subscriptionRepository.getSubscriptionsByStartDateBetween(startDate, endDate))
                .thenReturn(Arrays.asList(sub1, sub2));
        List<Subscription> result = subscriptionService.retrieveSubscriptionsByDates(startDate, endDate);
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(subscriptionRepository).getSubscriptionsByStartDateBetween(startDate, endDate);
    }
    @Test
    void testGetSubscriptionByTypeWithEmptyResult() {
        when(subscriptionRepository.findByTypeSubOrderByStartDateAsc(TypeSubscription.MONTHLY))
                .thenReturn(Collections.emptySet());
        Set<Subscription> result = subscriptionService.getSubscriptionByType(TypeSubscription.MONTHLY);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(subscriptionRepository).findByTypeSubOrderByStartDateAsc(TypeSubscription.MONTHLY);
    }
    @Test
    void testGetSubscriptionByType() {
        Subscription subscription = new Subscription();
        subscription.setTypeSub(TypeSubscription.ANNUAL);
        when(subscriptionRepository.findByTypeSubOrderByStartDateAsc(TypeSubscription.ANNUAL))
                .thenReturn(Set.of(subscription));
        Set<Subscription> result = subscriptionService.getSubscriptionByType(TypeSubscription.ANNUAL);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(subscription));
        verify(subscriptionRepository).findByTypeSubOrderByStartDateAsc(TypeSubscription.ANNUAL);
    }
}