package test;

import domain.Tessera;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TesseraTest {

    private Tessera tessera;

    @BeforeEach
    void setUp() {
        tessera = new Tessera(50.0); // tessera con credito iniziale
    }

    @Test
    void testCostruttoreEGetter() {
        assertEquals(50.0, tessera.getCredito(),
                "Il credito iniziale deve corrispondere al valore passato nel costruttore.");
    }

    @Test
    void testSetterAggiornaCredito() {
        tessera.setCredito(100.0);
        assertEquals(100.0, tessera.getCredito(),
                "Il metodo setCredito deve aggiornare correttamente il valore del credito.");
    }

    @Test
    void testSetterAccettaZero() {
        tessera.setCredito(0.0);
        assertEquals(0.0, tessera.getCredito(),
                "Il credito deve poter essere impostato a zero senza errori.");
    }

    @Test
    void testToStringContieneCredito() {
        String s = tessera.toString().toLowerCase();
        assertTrue(s.contains("credito"),
                "Il metodo toString deve contenere la parola 'credito'.");
        assertTrue(s.contains("50"),
                "Il metodo toString deve contenere il valore del credito.");
    }

    @Test
    void testToStringAggiornatoDopoSet() {
        tessera.setCredito(75.0);
        String s = tessera.toString();
        assertTrue(s.contains("75"),
                "Dopo aver aggiornato il credito, toString deve riflettere il nuovo valore.");
    }
}

