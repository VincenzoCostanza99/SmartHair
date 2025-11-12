package test;

import domain.Cliente;
import domain.Prenotazione;
import domain.Tessera;
import domain.Consiglio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente("Mario", "Rossi", "mario@rossi.it");
    }

    // === Test costruttore e getter di base ===
    @Test
    void testCostruttoreEGetter() {
        assertEquals("Mario", cliente.getNome());
        assertEquals("Rossi", cliente.getCognome());
        assertEquals("mario@rossi.it", cliente.getEmail());
        assertNotNull(cliente.getTessera(), "La tessera deve essere inizializzata nel costruttore");
        assertNotNull(cliente.getPrenotazioni(), "La lista prenotazioni deve essere inizializzata");
        assertFalse(cliente.isPrimoConsiglio(), "Il primo consiglio deve essere inizialmente false");
    }

    // === Test getConsiglio / setConsiglio ===
    @Test
    void testSetEGetConsiglio() {
        Consiglio c = new Consiglio();
        c.setServizio("taglio");
        cliente.setConsiglio(c);
        assertEquals("taglio", cliente.getConsiglio().getServizio());
    }

    // === Test setPrimoConsiglio ===
    @Test
    void testSetPrimoConsiglio() {
        assertFalse(cliente.isPrimoConsiglio());
        cliente.setPrimoConsiglio(true);
        assertTrue(cliente.isPrimoConsiglio(), "Il flag primoConsiglio deve essere aggiornato");
    }

    // === Test Tessera ===
    @Test
    void testGetTesseraValida() {
        Tessera t = cliente.getTessera();
        assertEquals(0.0, t.getCredito(), 0.001, "La tessera parte con credito zero");
    }
}
