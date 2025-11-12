package test;

import domain.Parrucchiere;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParrucchiereTest {

    // === Costruttore valido ===
    @Test
    void testCostruttoreValido() {
        Parrucchiere p = new Parrucchiere("Vincenzo");
        assertNotNull(p);
        assertEquals("Vincenzo", p.getNome());
    }

}
