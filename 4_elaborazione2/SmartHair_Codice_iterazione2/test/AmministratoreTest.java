package test;

import domain.Amministratore;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AmministratoreTest {

    @Test
    void testGetters() {
        Amministratore admin = new Amministratore("admin", "1234");
        assertEquals("admin", admin.getUsername(), "Il getter username deve restituire il valore corretto");
        assertEquals("1234", admin.getPassword(), "Il getter password deve restituire il valore corretto");
    }

    @Test
    void testVerificaCredenzialiCorrette() {
        Amministratore admin = new Amministratore("admin", "1234");
        assertTrue(admin.verificaCredenziali("admin", "1234"),
                "Le credenziali corrette devono restituire true");
    }

    @Test
    void testVerificaCredenzialiUsernameErrato() {
        Amministratore admin = new Amministratore("admin", "1234");
        assertFalse(admin.verificaCredenziali("wrongUser", "1234"),
                "Username errato deve restituire false");
    }

    @Test
    void testVerificaCredenzialiPasswordErrata() {
        Amministratore admin = new Amministratore("admin", "1234");
        assertFalse(admin.verificaCredenziali("admin", "wrongPass"),
                "Password errata deve restituire false");
    }

    @Test
    void testVerificaCredenzialiEntrambeErrate() {
        Amministratore admin = new Amministratore("admin", "1234");
        assertFalse(admin.verificaCredenziali("wrong", "wrong"),
                "Entrambe errate devono restituire false");
    }

    @Test
    void testToString() {
        Amministratore admin = new Amministratore("superUser", "0000");
        String result = admin.toString();
        assertTrue(result.contains("superUser"),
                "Il toString deve contenere lo username");
        assertTrue(result.startsWith("Amministratore"),
                "Il toString deve iniziare con 'Amministratore'");
    }
}

