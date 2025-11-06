package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import domain.Parrucchiere;

public class ParrucchiereTest {

    @Test
    void creaParrucchiere_valido() {
        Parrucchiere p = new Parrucchiere("Vincenzo");

        assertEquals("Vincenzo", p.getNome());
        assertNotNull(p.getOrariDisponibili());
        assertTrue(p.getOrariDisponibili().isEmpty());
    }

    @Test
    void setOrariDisponibili_funzionamentoCorretto() {
        Parrucchiere p = new Parrucchiere("Jose");

        Map<String, List<String>> orari = new HashMap<>();
        orari.put("martedi", Arrays.asList("9", "11", "15", "17"));
        p.setOrariDisponibili(orari);

        assertEquals(1, p.getOrariDisponibili().size());
        assertEquals(Arrays.asList("9", "11", "15", "17"), p.getOrariDisponibili().get("martedi"));
    }

    @Test
    void getOrariDisponibili_restituisceMappaModificabile() {
        Parrucchiere p = new Parrucchiere("Anna");
        Map<String, List<String>> orari = new HashMap<>();
        orari.put("venerdi", new ArrayList<>(Arrays.asList("9", "11")));
        p.setOrariDisponibili(orari);

        // Modifico la lista e controllo che il cambiamento si rifletta
        p.getOrariDisponibili().get("venerdi").add("15");

        assertTrue(p.getOrariDisponibili().get("venerdi").contains("15"));
    }

    @Test
    void toString_contieneNomeEOrari() {
        Parrucchiere p = new Parrucchiere("Luca");
        Map<String, List<String>> orari = new HashMap<>();
        orari.put("sabato", Arrays.asList("9", "11"));
        p.setOrariDisponibili(orari);

        String output = p.toString();
        assertTrue(output.contains("Luca"));
        assertTrue(output.contains("sabato"));
        assertTrue(output.contains("9"));
        assertTrue(output.contains("11"));
    }
}

