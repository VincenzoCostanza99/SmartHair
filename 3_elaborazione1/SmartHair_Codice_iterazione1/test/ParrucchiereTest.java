package test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import domain.Parrucchiere;

public class ParrucchiereTest {

    private Parrucchiere parrucchiere;

    @BeforeEach
    void setUp() {
        parrucchiere = new Parrucchiere("Vincenzo");
    }

    @Test
    void testCostruttoreNonAccettaNull() {
        assertThrows(NullPointerException.class, () -> new Parrucchiere(null));
    }

    @Test
    void testCostruttoreValidoImpostaNomeCorrettamente() {
        assertEquals("Vincenzo", parrucchiere.getNome(),
                "Il nome del parrucchiere deve corrispondere a quello passato al costruttore");
    }

    @Test
    void testGetNomeRestituisceValoreCorretto() {
        assertNotNull(parrucchiere.getNome(), "Il nome non deve essere null");
        assertEquals("Vincenzo", parrucchiere.getNome());
    }

    @Test
    void testToStringContieneNomeParrucchiere() {
        String toString = parrucchiere.toString().toLowerCase();
        assertTrue(toString.contains("vincenzo"), "toString() deve contenere il nome del parrucchiere");
    }

    @Test
    void testOggettiDiversiConStessoNomeSonoUgualiSoloSeEqualsDefinito() {
        Parrucchiere altro = new Parrucchiere("Vincenzo");
        // se equals non è ridefinito, i due oggetti saranno diversi
        assertNotEquals(parrucchiere, altro, "Due oggetti diversi con stesso nome non sono uguali se equals non è ridefinito");
    }

}
