package test;

import domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrenotazioneTest {

    private Cliente cliente;
    private Parrucchiere parrucchiere;

    @BeforeEach
    void setUp() {
        cliente = new Cliente("Mario", "Rossi", "mario@rossi.it");
        parrucchiere = new Parrucchiere("Gianni");
    }

    // === COSTRUTTORE VALIDO ===
    @Test
    void testCostruttoreValido() {
        Prenotazione p = new Prenotazione("10", "martedi", "taglio", cliente, parrucchiere, 25.0);
        assertEquals("10", p.getOrario());
        assertEquals("martedi", p.getGiorno());
        assertEquals("taglio", p.getServizio());
        assertEquals(cliente, p.getCliente());
        assertEquals(parrucchiere, p.getParrucchiere());
        assertEquals(25.0, p.getPrezzoServizio());
        assertTrue(p.getCodice() > 0);
    }

    // === COSTRUTTORE CON ORARIO NULL ===
    @Test
    void testCostruttoreOrarioNull() {
        assertThrows(NullPointerException.class,
                () -> new Prenotazione(null, "martedi", "taglio", cliente, parrucchiere, 25.0),
                "Orario null deve generare eccezione");
    }

    // === COSTRUTTORE CON GIORNO NULL ===
    @Test
    void testCostruttoreGiornoNull() {
        assertThrows(NullPointerException.class,
                () -> new Prenotazione("10", null, "taglio", cliente, parrucchiere, 25.0),
                "Giorno null deve generare eccezione");
    }

    // === COSTRUTTORE CON SERVIZIO NULL ===
    @Test
    void testCostruttoreServizioNull() {
        assertThrows(NullPointerException.class,
                () -> new Prenotazione("10", "martedi", null, cliente, parrucchiere, 25.0),
                "Servizio null deve generare eccezione");
    }

    // === COSTRUTTORE CON CLIENTE NULL ===
    @Test
    void testCostruttoreClienteNull() {
        assertThrows(NullPointerException.class,
                () -> new Prenotazione("10", "martedi", "taglio", null, parrucchiere, 25.0),
                "Cliente null deve generare eccezione");
    }

    // === TEST GETTER ===
    @Test
    void testGetters() {
        Prenotazione p = new Prenotazione("11", "venerdi", "colore", cliente, parrucchiere, 40.0);
        assertAll(
                () -> assertEquals("11", p.getOrario()),
                () -> assertEquals("venerdi", p.getGiorno()),
                () -> assertEquals("colore", p.getServizio()),
                () -> assertEquals(cliente, p.getCliente()),
                () -> assertEquals(parrucchiere, p.getParrucchiere()),
                () -> assertEquals(40.0, p.getPrezzoServizio())
        );
    }

    // === TEST CODICE UNIVOCO ===
    @Test
    void testCodiciDiversi() {
        Prenotazione p1 = new Prenotazione("9", "martedi", "taglio", cliente, parrucchiere, 20.0);
        Prenotazione p2 = new Prenotazione("11", "mercoledi", "colore", cliente, parrucchiere, 30.0);
        assertNotEquals(p1.getCodice(), p2.getCodice(), "Ogni prenotazione deve avere un codice univoco");
    }
}
