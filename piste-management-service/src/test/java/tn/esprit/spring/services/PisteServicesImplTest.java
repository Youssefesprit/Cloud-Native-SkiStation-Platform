package tn.esprit.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.spring.entities.Piste;
import tn.esprit.spring.repositories.IPisteRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PisteServicesImplTest {

    @Mock
    private IPisteRepository pisteRepository;

    @InjectMocks
    private PisteServicesImpl pisteServices;

    private Piste piste;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Créer un objet Piste fictif pour les tests
        piste = new Piste();
        piste.setNumPiste(1L);
        piste.setNamePiste("Piste Test");
    }

    @Test
    public void testRetrieveAllPistes() {
        when(pisteRepository.findAll()).thenReturn(Arrays.asList(piste));
        List<Piste> pistes = pisteServices.retrieveAllPistes();

        assertNotNull(pistes);
        assertEquals(1, pistes.size());
        assertEquals(piste.getNamePiste(), pistes.get(0).getNamePiste());

        verify(pisteRepository, times(1)).findAll();
    }

    @Test
    public void testAddMultiplePistes() {
        Piste piste2 = new Piste();
        piste2.setNumPiste(2L);
        piste2.setNamePiste("Piste Test 2");

        when(pisteRepository.save(any(Piste.class))).thenReturn(piste, piste2);

        Piste savedPiste1 = pisteServices.addPiste(piste);
        Piste savedPiste2 = pisteServices.addPiste(piste2);

        assertNotNull(savedPiste1);
        assertNotNull(savedPiste2);
        assertEquals("Piste Test", savedPiste1.getNamePiste());
        assertEquals("Piste Test 2", savedPiste2.getNamePiste());

        verify(pisteRepository, times(2)).save(any(Piste.class));
    }

    @Test
    public void testRetrieveNonExistentPiste() {
        when(pisteRepository.findById(2L)).thenReturn(Optional.empty());

        Piste foundPiste = pisteServices.retrievePiste(2L);

        assertNull(foundPiste);
        verify(pisteRepository, times(1)).findById(2L);
    }

    @Test
    public void testUpdatePiste() {
        when(pisteRepository.findById(1L)).thenReturn(Optional.of(piste));
        when(pisteRepository.save(any(Piste.class))).thenReturn(piste);

        piste.setNamePiste("Piste Updated");
        Piste updatedPiste = pisteServices.addPiste(piste);

        assertEquals("Piste Updated", updatedPiste.getNamePiste());
        verify(pisteRepository, times(1)).save(any(Piste.class));
    }

    @Test
    public void testRemoveNonExistentPiste() {
        doNothing().when(pisteRepository).deleteById(2L);

        pisteServices.removePiste(2L);

        verify(pisteRepository, times(1)).deleteById(2L);
    }

    @Test
    public void testRetrievePisteWithException() {
        when(pisteRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            pisteServices.retrievePiste(1L);
        });

        assertEquals("Database error", exception.getMessage());
        verify(pisteRepository, times(1)).findById(1L);
    }
}
