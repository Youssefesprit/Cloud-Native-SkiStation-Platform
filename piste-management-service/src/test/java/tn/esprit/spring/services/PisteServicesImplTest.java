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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        // Initialiser les objets mock
        MockitoAnnotations.openMocks(this);

        // Créer un objet Piste fictif pour les tests
        piste = new Piste();
        piste.setNumPiste(1L);
        piste.setNamePiste("Piste Test");
        // vous pouvez initialiser d'autres champs ici
    }

    @Test
    public void testRetrieveAllPistes() {
        // Configurer le comportement du mock
        when(pisteRepository.findAll()).thenReturn(Arrays.asList(piste));

        // Appeler la méthode de service
        List<Piste> pistes = pisteServices.retrieveAllPistes();

        // Vérifier que le résultat est correct
        assertNotNull(pistes);
        assertEquals(1, pistes.size());
        assertEquals(piste.getNamePiste(), pistes.get(0).getNamePiste());

        // Vérifier que la méthode findAll du repository a été appelée une fois
        verify(pisteRepository, times(1)).findAll();
    }

    @Test
    public void testAddPiste() {
        // Configurer le mock pour sauvegarder une piste
        when(pisteRepository.save(any(Piste.class))).thenReturn(piste);

        // Appeler la méthode de service
        Piste savedPiste = pisteServices.addPiste(piste);

        // Vérifier que l'objet retourné est correct
        assertNotNull(savedPiste);
        assertEquals(piste.getNamePiste(), savedPiste.getNamePiste());

        // Vérifier que la méthode save du repository a été appelée une fois
        verify(pisteRepository, times(1)).save(any(Piste.class));
    }

    @Test
    public void testRemovePiste() {
        // Appeler la méthode de service pour supprimer une piste
        pisteServices.removePiste(1L);

        // Vérifier que la méthode deleteById a été appelée avec le bon argument
        verify(pisteRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testRetrievePiste() {
        // Configurer le mock pour retourner une piste
        when(pisteRepository.findById(1L)).thenReturn(Optional.of(piste));

        // Appeler la méthode de service
        Piste foundPiste = pisteServices.retrievePiste(1L);

        // Vérifier que l'objet retourné est correct
        assertNotNull(foundPiste);
        assertEquals(piste.getNamePiste(), foundPiste.getNamePiste());

        // Vérifier que la méthode findById a été appelée une fois
        verify(pisteRepository, times(1)).findById(1L);
    }
}
