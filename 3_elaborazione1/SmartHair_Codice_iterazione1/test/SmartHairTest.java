package test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.Cliente;
import domain.Prenotazione;
import domain.SmartHair;
import domain.Tessera;

public class SmartHairTest {

    private SmartHair sm;

    /**
     * Resetta il singleton SmartHair prima di ogni test,
     * in modo da avere sempre stato pulito.
     */
    @BeforeEach
    void resetSingleton() throws Exception {
        Field f = SmartHair.class.getDeclaredField("instance");
        f.setAccessible(true);
        f.set(null, null);// azzera la reference
        sm = SmartHair.getInstance(); // ricrea l'istanza
    }

    // ---------- REGISTRAZIONE / CREDENZIALI ----------

    @Test
    void nuovoCliente_valido() {
        Cliente c = sm.nuovoCliente("Mario", "Rossi", "mario@mail.com", "password123");
        assertNotNull(c);
        assertEquals("mario@mail.com", c.getEmail());
    }

    @Test
    void nuovoCliente_nome_vuoto() {
        Cliente c = sm.nuovoCliente("  ", "Rossi", "m@mail.com", "password123");
        assertNull(c);
    }

    @Test
    void nuovoCliente_cognome_vuoto() {
        Cliente c = sm.nuovoCliente("Mario", "   ", "m@mail.com", "password123");
        assertNull(c);
    }

    @Test
    void nuovoCliente_email_non_valida() {
        Cliente c = sm.nuovoCliente("Mario", "Rossi", "email_senza_at", "password123");
        assertNull(c);
    }

    @Test
    void nuovoCliente_password_troppo_corta() {
        Cliente c = sm.nuovoCliente("Mario", "Rossi", "m@mail.com", "short");
        assertNull(c);
    }

    @Test
    void nuovoCliente_duplicato() {
        sm.nuovoCliente("Mario", "Rossi", "m@mail.com", "password123");
        Cliente c2 = sm.nuovoCliente("Mario", "Rossi", "m@mail.com", "password123");
        assertNull(c2);
    }

    // ---------- ACCESSO ----------

    @Test
    void accesso_valido() {
        sm.nuovoCliente("Luigi", "Verdi", "l@mail.com", "password123");
        assertTrue(sm.accesso("l@mail.com", "password123"));
    }

    @Test
    void accesso_ko_email_sbagliata() {
        sm.nuovoCliente("Luigi", "Verdi", "l@mail.com", "password123");
        assertFalse(sm.accesso("x@mail.com", "password123"));
    }

    @Test
    void accesso_ko_password_sbagliata() {
        sm.nuovoCliente("Luigi", "Verdi", "l@mail.com", "password123");
        assertFalse(sm.accesso("l@mail.com", "wrongPass"));
    }

    // ---------- LISTINO ----------

    @Test
    void listinoPrezzi_inizializzato() {
        Map<String, Integer> listino = sm.getListinoPrezzi();
        assertEquals(3, listino.size());
        assertEquals(20, listino.get("taglio"));
        assertEquals(40, listino.get("colore"));
        assertEquals(10, listino.get("piega"));
    }

    // ---------- RICARICA TESSERA ----------

    @Test
    void ricaricaTessera_valida() {
        sm.nuovoCliente("Anna", "Bianchi", "a@mail.com", "password123");
        Tessera t = sm.esisteTessera("a@mail.com");
        assertNotNull(t);
        assertEquals(0.0, t.getCredito(), 0.0001);

        assertTrue(sm.ricaricaTessera(50, "a@mail.com"));
        assertEquals(50.0, t.getCredito(), 0.0001);
    }

    @Test
    void ricaricaTessera_importo_non_valido() {
        sm.nuovoCliente("Anna", "Bianchi", "a@mail.com", "password123");
        assertFalse(sm.ricaricaTessera(0, "a@mail.com"));
        assertFalse(sm.ricaricaTessera(-10, "a@mail.com"));
    }

    @Test
    void ricaricaTessera_email_inesistente() {
        assertFalse(sm.ricaricaTessera(20, "no@mail.com"));
    }

    // ---------- SELEZIONA SERVIZIO ----------

    @Test
    void selezionaServizio_servizio_non_valido() {
        sm.nuovoCliente("Anna", "Bianchi", "a@mail.com", "password123");
        assertNull(sm.selezionaServizio("a@mail.com", "permanente")); // non è in listino
    }

    @Test
    void selezionaServizio_credito_insufficiente() {
        sm.nuovoCliente("Anna", "Bianchi", "a@mail.com", "password123");
        // nessuna ricarica -> credito 0
        assertNull(sm.selezionaServizio("a@mail.com", "taglio")); // prezzo 20
    }

    @Test
    void selezionaServizio_ok_restituisce_orari() {
        sm.nuovoCliente("Anna", "Bianchi", "a@mail.com", "password123");
        sm.ricaricaTessera(100, "a@mail.com");
        Map<String, Map<String, List<String>>> orari = sm.selezionaServizio("a@mail.com", "colore");
        assertNotNull(orari);
        assertTrue(orari.containsKey("vincenzo"));
        assertTrue(orari.containsKey("jose"));
        assertTrue(orari.get("vincenzo").get("martedi").contains("9"));
    }

    // ---------- SELEZIONA ORARIO ----------

    @Test
    void selezionaOrario_ok_e_rimozione() {
        sm.nuovoCliente("Anna", "Bianchi", "a@mail.com", "password123");
        sm.ricaricaTessera(100, "a@mail.com");
        sm.selezionaServizio("a@mail.com", "taglio");

        // l'orario "9" del martedì per Vincenzo esiste
        String scelto = sm.selezionaOrario("martedi", "9", "vincenzo");
        assertEquals("9", scelto);

        // dopo la rimozione non deve essere più selezionabile
        String diNuovo = sm.selezionaOrario("martedi", "9", "vincenzo");
        assertNull(diNuovo);
    }

    @Test
    void selezionaOrario_dipendente_inesistente() {
        sm.nuovoCliente("Anna", "Bianchi", "a@mail.com", "password123");
        sm.ricaricaTessera(100, "a@mail.com");
        sm.selezionaServizio("a@mail.com", "taglio");

        assertNull(sm.selezionaOrario("martedi", "9", "paolo"));
    }

    @Test
    void selezionaOrario_giorno_non_valido() {
        sm.nuovoCliente("Anna", "Bianchi", "a@mail.com", "password123");
        sm.ricaricaTessera(100, "a@mail.com");
        sm.selezionaServizio("a@mail.com", "taglio");

        assertNull(sm.selezionaOrario("domenica", "9", "vincenzo"));
    }

    @Test
    void selezionaOrario_orario_non_valido() {
        sm.nuovoCliente("Anna", "Bianchi", "a@mail.com", "password123");
        sm.ricaricaTessera(100, "a@mail.com");
        sm.selezionaServizio("a@mail.com", "taglio");

        assertNull(sm.selezionaOrario("martedi", "20", "vincenzo"));
    }

    // ---------- CONFERMA ORARIO + PRENOTAZIONI ----------

    @Test
    void confermaOrario_creaPrenotazione() {
        sm.nuovoCliente("Anna", "Bianchi", "a@mail.com", "password123");
        sm.ricaricaTessera(100, "a@mail.com");
        sm.selezionaServizio("a@mail.com", "taglio");

        String orario = sm.selezionaOrario("martedi", "11", "jose");
        assertNotNull(orario);

        sm.confermaOrario(orario, "martedi", "jose", "a@mail.com", "taglio");

        List<Prenotazione> mie = sm.getPrenotazioniCliente("a@mail.com");
        assertEquals(1, mie.size());
        Prenotazione p = mie.get(0);
        assertEquals("martedi", p.getGiorno());
        assertEquals("11", p.getOrario());
        assertEquals("jose", p.getDipendente());
        assertEquals("taglio", p.getServizio());
    }

    @Test
    void getPrenotazioniCliente_vuota() {
        sm.nuovoCliente("A", "B", "x@mail.com", "password123");
        List<Prenotazione> mie = sm.getPrenotazioniCliente("x@mail.com");
        assertTrue(mie.isEmpty());
    }

    // ---------- CANCELLA PRENOTAZIONE (e ripristino orario) ----------

    @Test
    void cancellaPrenotazione_ok_e_ripristino_orario() {
        sm.nuovoCliente("A", "B", "c@mail.com", "password123");
        sm.ricaricaTessera(100, "c@mail.com");
        sm.selezionaServizio("c@mail.com", "piega");

        // seleziono un orario che poi cancellerò
        String scelto = sm.selezionaOrario("giovedi", "15", "vincenzo");
        sm.confermaOrario(scelto, "giovedi", "vincenzo", "c@mail.com", "piega");

        List<Prenotazione> mie = sm.getPrenotazioniCliente("c@mail.com");
        assertEquals(1, mie.size());
        int codice = mie.get(0).getCodice();

        // cancello
        assertTrue(sm.cancellaPrenotazione(codice, "c@mail.com"));

        // dopo la cancellazione, lo stesso slot torna disponibile
        String diNuovo = sm.selezionaOrario("giovedi", "15", "vincenzo");
        assertEquals("15", diNuovo);
    }

    @Test
    void cancellaPrenotazione_non_trovata() {
        sm.nuovoCliente("A", "B", "c@mail.com", "password123");
        assertFalse(sm.cancellaPrenotazione(9999, "c@mail.com"));
    }
}

