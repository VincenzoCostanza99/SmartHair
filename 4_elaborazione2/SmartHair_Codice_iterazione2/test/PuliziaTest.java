package test;

import domain.Pulizia;
import domain.Parrucchiere;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PuliziaTest {

    @Test
    public void testCostruttoreValido() {
        Parrucchiere p = new Parrucchiere("Luca");
        Pulizia pulizia = new Pulizia("venerdi", p);

        assertEquals("venerdi", pulizia.getGiorno(), "Il giorno deve essere assegnato correttamente");
        assertEquals(p, pulizia.getAssegnato(), "Il parrucchiere assegnato deve corrispondere");
    }

    @Test
    public void testGettersValoriCorrettamenteRestituiti() {
        Parrucchiere p = new Parrucchiere("Giulia");
        Pulizia pulizia = new Pulizia("sabato", p);

        assertEquals("sabato", pulizia.getGiorno());
        assertEquals("Giulia", pulizia.getAssegnato().getNome());
    }

    @Test
    public void testToStringContieneDatiCorretti() {
        Parrucchiere p = new Parrucchiere("Sara");
        Pulizia pulizia = new Pulizia("martedi", p);

        String output = pulizia.toString();
        assertTrue(output.contains("martedi"), "Il toString deve contenere il giorno");
        assertTrue(output.contains("Sara"), "Il toString deve contenere il nome del parrucchiere");
        assertTrue(output.startsWith("Pulizia{"), "Il toString deve iniziare correttamente");
    }

}

