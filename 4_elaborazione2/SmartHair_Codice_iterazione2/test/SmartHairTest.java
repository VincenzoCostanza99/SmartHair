package test;

import domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SmartHairTest {

    private SmartHair sistema;

    @BeforeEach
    public void setup() {
        sistema = SmartHair.getInstance();

        // reset minimo: svuotiamo alcune strutture dove serve
        sistema.recuperaGiorniSenzaPulizia().clear();
        sistema.recuperaGiorniSenzaPulizia().addAll(Arrays.asList("martedi", "mercoledi", "giovedi", "venerdi", "sabato"));
    }

    // --- TEST 1: SINGLETON ---
    @Test
    public void testSingleton() {
        SmartHair a = SmartHair.getInstance();
        SmartHair b = SmartHair.getInstance();
        assertSame(a, b, "SmartHair deve essere un singleton");
    }

    // --- TEST 2: ACCESSO ADMIN ---
    @Test
    public void testAccessoAdminCorretto() {
        boolean risultato = sistema.AccessoAdmin("admin@gmail.com", "admin1234");
        assertTrue(risultato, "Le credenziali admin corrette devono permettere l'accesso");
        assertTrue(sistema.isAdminLoggato());
        sistema.logoutAdmin();
    }

    @Test
    public void testAccessoAdminEmailErrata() {
        boolean risultato = sistema.AccessoAdmin("fake@gmail.com", "admin1234");
        assertFalse(risultato, "Email errata deve negare l'accesso");
        assertFalse(sistema.isAdminLoggato());
    }

    @Test
    public void testLogoutAdmin() {
        sistema.AccessoAdmin("admin@gmail.com", "admin1234");
        sistema.logoutAdmin();
        assertFalse(sistema.isAdminLoggato());
    }

    // --- TEST 3: TURNI DI PULIZIA ---
    @Test
    public void testInserisciDatiValido() throws Exception {
        Parrucchiere p = new Parrucchiere("Luca");

        // Aggiungi "Luca" all'elenco parruccchieri di SmartHair
        var campoParr = SmartHair.class.getDeclaredField("elencoParrucchieri");
        campoParr.setAccessible(true);
        List<Parrucchiere> parrList = new ArrayList<>();
        parrList.add(p);
        campoParr.set(sistema, parrList);

        // Assicurati che il giorno sia disponibile
        var campoGiorni = SmartHair.class.getDeclaredField("giorniSenzaPulizia");
        campoGiorni.setAccessible(true);
        List<String> giorni = new ArrayList<>(Arrays.asList("martedi", "mercoledi", "giovedi", "venerdi", "sabato"));
        campoGiorni.set(sistema, giorni);

        boolean risultato = sistema.inserisciDati("Luca", "martedi");
        assertTrue(risultato, "L'inserimento corretto deve riuscire");
    }

    @Test
    public void testInserisciDatiParrucchiereInesistente() {
        boolean risultato = sistema.inserisciDati("Fake", "mercoledi");
        assertFalse(risultato, "Parrucchiere non esistente deve fallire");
    }

    @Test
    public void testInserisciDatiGiornoNonDisponibile() {
        Parrucchiere p = new Parrucchiere("Anna");
        sistema.recuperaGiorniSenzaPulizia().remove("sabato"); // lo togliamo per simulare assegnazione
        boolean risultato = sistema.inserisciDati("Anna", "sabato");
        assertFalse(risultato, "Giorno non disponibile deve restituire false");
    }

    @Test
    public void testDetrazioneCredito() {
        // Il cliente ha già una tessera inizializzata a 0
        Cliente c = new Cliente("Mario", "Rossi", "mario@rossi.it");
        c.getTessera().aggiornaSaldo(50.0); // aggiungiamo credito iniziale

        try {
            var campoCliente = SmartHair.class.getDeclaredField("clienteCorrente");
            campoCliente.setAccessible(true);
            campoCliente.set(sistema, c);
        } catch (Exception ignored) {}

        boolean risultato = sistema.detrazioneCredito(20.0);
        assertTrue(risultato, "Detrazione deve riuscire se il credito è sufficiente");
        assertEquals(30.0, c.getTessera().getCredito(), 0.001);
    }


    @Test
    public void testDetrazioneCreditoInsufficiente() {
        Cliente c = new Cliente("Luca", "Bianchi", "luca@gmail.com");
        c.getTessera().aggiornaSaldo(10.0); // solo 10$

        try {
            var campoCliente = SmartHair.class.getDeclaredField("clienteCorrente");
            campoCliente.setAccessible(true);
            campoCliente.set(sistema, c);
        } catch (Exception ignored) {}

        boolean risultato = sistema.detrazioneCredito(40.0);
        assertFalse(risultato, "Credito insufficiente deve restituire false");
        assertEquals(10.0, c.getTessera().getCredito(), 0.001, "Il credito non deve cambiare");
    }


    @Test
    public void testRimborsoCredito() {
        Cliente c = new Cliente("Anna", "Verdi", "anna@verdi.it");
        c.getTessera().aggiornaSaldo(30.0); // credito iniziale

        try {
            var campoCliente = SmartHair.class.getDeclaredField("clienteCorrente");
            campoCliente.setAccessible(true);
            campoCliente.set(sistema, c);
        } catch (Exception ignored) {}

        boolean risultato = sistema.rimborsoCredito(20.0);
        assertTrue(risultato, "Il rimborso deve riuscire con importo positivo");
        assertEquals(50.0, c.getTessera().getCredito(), 0.001);
    }


    @Test
    public void testRimborsoCreditoNegativo() {
        boolean risultato = sistema.rimborsoCredito(-10.0);
        assertFalse(risultato, "Rimborso negativo deve fallire");
    }

    // --- TEST 5: PRESENZA ORARI DISPONIBILI ---
    @Test
    public void testPresenzaOrariDisponibiliVuota() throws Exception {
        // Forziamo una mappa completamente vuota
        var campo = SmartHair.class.getDeclaredField("mappaOrariDip");
        campo.setAccessible(true);
        campo.set(sistema, new HashMap<>());

        boolean risultato = sistema.presenzaOrariDisponibili();
        assertFalse(risultato, "Senza mappa orari non ci sono disponibilità");
    }

    @Test
    public void testPresenzaOrariDisponibiliPiena() throws Exception {
        Map<String, List<String>> giorni = new HashMap<>();
        giorni.put("martedi", Arrays.asList("9", "10"));
        Map<String, Map<String, List<String>>> mappa = new HashMap<>();
        mappa.put("Luca", giorni);

        var campo = SmartHair.class.getDeclaredField("mappaOrariDip");
        campo.setAccessible(true);
        campo.set(sistema, mappa);

        assertTrue(sistema.presenzaOrariDisponibili(), "Con orari disponibili deve restituire true");
    }

    // --- TEST 6: GENERAZIONE CONSIGLIO ---
    @Test
    public void testGeneraServizioDaUltimaPrenotazione() throws Exception {
        Cliente c = new Cliente("Mario", "Rossi", "mario@rossi.it");
        Parrucchiere p = new Parrucchiere("Luca");
        Prenotazione pren = new Prenotazione("10", "venerdi", "taglio", c, p, 30.0);
        c.aggiungiPrenotazione(pren);

        var campoCliente = SmartHair.class.getDeclaredField("clienteCorrente");
        campoCliente.setAccessible(true);
        campoCliente.set(sistema, c);

        String consiglio = sistema.generaServizio();
        assertNotNull(consiglio, "Il consiglio non deve essere nullo");
        assertTrue(Arrays.asList("piega", "colore", "taglio").contains(consiglio));
    }
}

