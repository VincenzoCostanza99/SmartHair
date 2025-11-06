package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import domain.Cliente;

public class ClienteTest {

    @Test
    void creaCliente_valido() {
        Cliente c = new Cliente("Mario", "Rossi", "mario@mail.com");

        assertEquals("Mario", c.getNome());
        assertEquals("Rossi", c.getCognome());
        assertEquals("mario@mail.com", c.getEmail());
        assertEquals("Cliente{Mario Rossi, mario@mail.com}", c.toString());
    }

    @Test
    void creaCliente_conNull_lanciaEccezione() {
        assertThrows(NullPointerException.class, () -> new Cliente(null, "Rossi", "mario@mail.com"));
        assertThrows(NullPointerException.class, () -> new Cliente("Mario", null, "mario@mail.com"));
        assertThrows(NullPointerException.class, () -> new Cliente("Mario", "Rossi", null));
    }

    @Test
    void getMetodi_restituisconoValoriCorretti() {
        Cliente c = new Cliente("Luca", "Bianchi", "luca@mail.com");

        assertAll(
            () -> assertEquals("Luca", c.getNome()),
            () -> assertEquals("Bianchi", c.getCognome()),
            () -> assertEquals("luca@mail.com", c.getEmail())
        );
    }
}
