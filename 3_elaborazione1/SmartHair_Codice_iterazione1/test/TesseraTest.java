package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import domain.Tessera;

public class TesseraTest {

    @Test
    void creaTessera_valoriInizialiCorretti() {
        Tessera t = new Tessera("mario@mail.com");

        assertEquals("mario@mail.com", t.getEmail());
        assertEquals(0.0, t.getCredito());
    }

    @Test
    void setCredito_modificaValoreCorrettamente() {
        Tessera t = new Tessera("anna@mail.com");
        t.setCredito(50.0);

        assertEquals(50.0, t.getCredito());
    }

    @Test
    void setEmail_modificaEmailCorrettamente() {
        Tessera t = new Tessera("vecchia@mail.com");
        t.setEmail("nuova@mail.com");

        assertEquals("nuova@mail.com", t.getEmail());
    }

    @Test
    void toString_contieneEmailECredito() {
        Tessera t = new Tessera("luca@mail.com");
        t.setCredito(30.5);

        String testo = t.toString();
        assertTrue(testo.contains("luca@mail.com"));
        assertTrue(testo.contains("30.5"));
        assertTrue(testo.contains("Tessera"));
    }
}
