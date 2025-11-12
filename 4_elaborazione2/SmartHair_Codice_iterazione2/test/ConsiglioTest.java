package test;

import domain.Consiglio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConsiglioTest {

    private Consiglio consiglio;

    @BeforeEach
    void setUp() {
        consiglio = new Consiglio();
    }

    // === Test costruttore vuoto ===
    @Test
    void testCostruttoreVuoto() {
        assertNull(consiglio.getServizio(), "Il servizio deve essere nullo nel costruttore vuoto");
    }

    // === Test costruttore con parametro valido ===
    @Test
    void testCostruttoreConParametroValido() {
        Consiglio c = new Consiglio("Taglio");
        assertEquals("Taglio", c.getServizio(), "Il servizio deve essere inizializzato correttamente");
    }

    // === Test setServizio con valore valido ===
    @Test
    void testSetServizioValido() {
        consiglio.setServizio("Piega");
        assertEquals("Piega", consiglio.getServizio(), "Il setter deve aggiornare correttamente il servizio");
    }

    // === Test toString ===
    @Test
    void testToStringContieneServizio() {
        consiglio.setServizio("Colore");
        String result = consiglio.toString();
        assertTrue(result.contains("Consiglio"), "Il toString deve contenere il nome della classe");
        assertTrue(result.contains("Colore"), "Il toString deve contenere il valore del servizio");
    }
}
