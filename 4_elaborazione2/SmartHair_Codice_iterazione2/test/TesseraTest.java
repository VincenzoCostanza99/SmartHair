package test;

import domain.Tessera;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TesseraTest {

    // --- Test costruttori ---
    @Test
    public void testCostruttoreConParametro() {
        Tessera t = new Tessera(50.0);
        assertEquals(50.0, t.getCredito(), 0.001, "Il credito iniziale deve essere impostato correttamente");
    }

    @Test
    public void testCostruttoreVuoto() {
        Tessera t = new Tessera();
        assertEquals(0.0, t.getCredito(), 0.001, "Il costruttore vuoto deve inizializzare a 0");
    }

    // --- Test getter e setter ---
    @Test
    public void testSetEGetCredito() {
        Tessera t = new Tessera();
        t.setCredito(100.0);
        assertEquals(100.0, t.getCredito(), 0.001, "Il setter deve aggiornare correttamente il credito");
    }

    // --- Test aggiornaSaldo ---
    @Test
    public void testAggiornaSaldoPositivo() {
        Tessera t = new Tessera(20.0);
        t.aggiornaSaldo(30.0);
        assertEquals(50.0, t.getCredito(), 0.001, "L'aggiornamento positivo deve aumentare il credito");
    }

    @Test
    public void testAggiornaSaldoNegativo() {
        Tessera t = new Tessera(40.0);
        t.aggiornaSaldo(-10.0);
        assertEquals(30.0, t.getCredito(), 0.001, "L'aggiornamento negativo deve ridurre il credito");
    }

    // --- Test toString ---
    @Test
    public void testToStringContieneCredito() {
        Tessera t = new Tessera(25.5);
        String output = t.toString();
        assertTrue(output.contains("credito=25.5"), "Il toString deve contenere il valore del credito");
        assertTrue(output.contains("Tessera{"), "Il toString deve iniziare con 'Tessera{'");
    }
}
