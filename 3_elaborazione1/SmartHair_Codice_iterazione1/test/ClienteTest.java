package test;
import domain.Cliente;
import domain.Parrucchiere;
import domain.Prenotazione;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class ClienteTest {

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        // Inizializzazione prima di ogni test
        cliente = new Cliente("Mario", "Rossi", "mario.rossi@example.com");
    }

    // --- Test costruttore e getter ---
    @Test
    void testCostruttoreEGetter() {
        assertEquals("Mario", cliente.getNome());
        assertEquals("Rossi", cliente.getCognome());
        assertEquals("mario.rossi@example.com", cliente.getEmail());
        assertNotNull(cliente.getTessera(), "La tessera deve essere inizializzata");
        assertEquals(0.0, cliente.getTessera().getCredito(), 0.001);
        assertTrue(cliente.getPrenotazioni().isEmpty(), "La lista prenotazioni deve inizialmente essere vuota");
    }

    // --- Test aggiunta prenotazione ---
    @Test
    void testAggiungiPrenotazione() {
        Parrucchiere parrucchiere = new Parrucchiere("Vincenzo");
        Prenotazione p = new Prenotazione("9", "martedi", "taglio", cliente, parrucchiere);

        cliente.aggiungiPrenotazione(p);

        List<Prenotazione> prenotazioni = cliente.getPrenotazioni();
        assertEquals(1, prenotazioni.size());
        assertSame(p, prenotazioni.get(0), "La prenotazione aggiunta deve essere nella lista");
    }

    // --- Test rimozione prenotazione ---
    @Test
    void testRimuoviPrenotazione() {
        Parrucchiere parrucchiere = new Parrucchiere("Jose");
        Prenotazione p = new Prenotazione("11", "mercoledi", "colore", cliente, parrucchiere);

        cliente.aggiungiPrenotazione(p);
        cliente.rimuoviPrenotazione(p);

        assertTrue(cliente.getPrenotazioni().isEmpty(), "La lista deve essere vuota dopo la rimozione");
    }

    // --- Test aggiunta prenotazione nulla ---
    @Test
    void testAggiungiPrenotazioneNull() {
        cliente.aggiungiPrenotazione(null);
        assertTrue(cliente.getPrenotazioni().isEmpty(), "Non deve aggiungere prenotazioni nulle");
    }

    // --- Test toString ---
    @Test
    void testToStringContieneEmailENome() {
        String output = cliente.toString();
        assertTrue(output.contains("mario.rossi@example.com"));
        assertTrue(output.contains("Mario"));
    }

    // --- Test stampaPrenotazioni (solo verifica logica) ---
    @Test
    void testStampaPrenotazioniVuote() {
        // Nessuna prenotazione
        assertTrue(cliente.getPrenotazioni().isEmpty());
        // Non si può verificare direttamente l'output console in questo test
    }
}
