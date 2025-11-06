package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import domain.Prenotazione;

public class PrenotazioneTest {

    @BeforeEach
    void resetContatore() throws Exception {
        // resetta il contatore statico tra i test per evitare interferenze
        java.lang.reflect.Field contatore = Prenotazione.class.getDeclaredField("contatoreCodici");
        contatore.setAccessible(true);
        contatore.setInt(null, 1);
    }

    @Test
    void creaPrenotazione_valoriCorretti() {
        Prenotazione p = new Prenotazione("9", "martedi", "vincenzo", "mario@mail.com", "taglio");

        assertEquals(1, p.getCodice());
        assertEquals("9", p.getOrario());
        assertEquals("martedi", p.getGiorno());
        assertEquals("vincenzo", p.getDipendente());
        assertEquals("mario@mail.com", p.getEmail());
        assertEquals("taglio", p.getServizio());
    }

    @Test
    void codici_incrementanoAutomaticamente() {
        Prenotazione p1 = new Prenotazione("9", "martedi", "vincenzo", "mario@mail.com", "taglio");
        Prenotazione p2 = new Prenotazione("11", "mercoledi", "jose", "anna@mail.com", "colore");
        Prenotazione p3 = new Prenotazione("15", "giovedi", "vincenzo", "luca@mail.com", "piega");

        assertEquals(1, p1.getCodice());
        assertEquals(2, p2.getCodice());
        assertEquals(3, p3.getCodice());
    }

    @Test
    void toString_contieneInformazioniChiave() {
        Prenotazione p = new Prenotazione("17", "sabato", "jose", "test@mail.com", "colore");
        String testo = p.toString();

        assertTrue(testo.contains("Prenotazione"));
        assertTrue(testo.contains("sabato"));
        assertTrue(testo.contains("17"));
        assertTrue(testo.contains("jose"));
        assertTrue(testo.contains("colore"));
        assertTrue(testo.contains("test@mail.com"));
    }
}

