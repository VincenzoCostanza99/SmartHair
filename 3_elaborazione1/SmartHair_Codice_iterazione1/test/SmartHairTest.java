package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import domain.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

public class SmartHairTest {

    private SmartHair sm;
    private Cliente cliente;

    @BeforeEach
    void setUp() throws Exception {
        //reset completo del Singleton tramite reflection
        java.lang.reflect.Field instanceField = SmartHair.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        sm = SmartHair.getInstance();
        cliente = sm.nuovoCliente("Mario", "Rossi", "mario.rossi@email.com", "password123");
        assertNotNull(cliente, "Il cliente deve essere stato creato correttamente");
    }


    @Test
    void testNuovoClienteRegistraCorrettamente() {
        assertEquals("Mario", cliente.getNome());
        assertTrue(cliente.getTessera().getCredito() == 0.0);
        assertTrue(cliente.getPrenotazioni().isEmpty());
    }

    @Test
    void testAccessoCorrettoEImpostaClienteCorrente() {
        boolean accesso = sm.accesso("mario.rossi@email.com", "password123");
        assertTrue(accesso);
        assertNotNull(sm.getClienteCorrente());
        assertEquals("Mario", sm.getClienteCorrente().getNome());
    }

    @Test
    void testAccessoFallisceConPasswordErrata() {
        boolean accesso = sm.accesso("mario.rossi@email.com", "sbagliata");
        assertFalse(accesso);
        assertNull(sm.getClienteCorrente(), "Il cliente corrente deve essere nullo se login fallisce");
    }

    @Test
    void testRicaricaTesseraAggiornaCredito() {
        sm.accesso("mario.rossi@email.com", "password123");
        double creditoIniziale = cliente.getTessera().getCredito();

        boolean ok = sm.ricaricaTessera(50.0);
        assertTrue(ok);
        assertEquals(creditoIniziale + 50.0, cliente.getTessera().getCredito());
    }

    @Test
    void testRicaricaTesseraRifiutaValoriNegativi() {
        sm.accesso("mario.rossi@email.com", "password123");
        boolean ok = sm.ricaricaTessera(-10);
        assertFalse(ok);
    }

    @Test
    void testLogoutReimpostaClienteCorrente() {
        sm.accesso("mario.rossi@email.com", "password123");
        assertNotNull(sm.getClienteCorrente());
        sm.logout();
        assertNull(sm.getClienteCorrente(), "Dopo il logout, clienteCorrente deve essere nullo");
    }

    @Test
    void testConfermaPrenotazioneCreaNuovaPrenotazione() {
        sm.accesso("mario.rossi@email.com", "password123");
        sm.ricaricaTessera(100.0);
        sm.confermaOrario("9", "martedi", "vincenzo", "taglio");

        List<Prenotazione> prenotazioni = cliente.getPrenotazioni();
        assertFalse(prenotazioni.isEmpty(), "Dopo la conferma deve esistere almeno una prenotazione");
    }

    @Test
    void testCancellaPrenotazioneFunzionaCorrettamente() {
        sm.accesso("mario.rossi@email.com", "password123");
        sm.ricaricaTessera(100.0);
        sm.confermaOrario("11", "giovedi", "vincenzo", "piega");

        List<Prenotazione> prenotazioni = cliente.getPrenotazioni();
        int codice = prenotazioni.get(0).getCodice();

        boolean cancellata = sm.cancellaPrenotazione(codice);
        assertTrue(cancellata, "La prenotazione deve essere rimossa correttamente");
    }

    @Test
    void testStampaPrenotazioniClienteMostraPrenotazioniCorrette() {
        sm.accesso("mario.rossi@email.com", "password123");
        sm.ricaricaTessera(100.0);
        sm.confermaOrario("9", "martedi", "vincenzo", "taglio");

        // Cattura l’output della console
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        sm.stampaPrenotazioniCliente();

        System.setOut(System.out); // ripristina la console standard

        // Analizza l'output catturato
        String output = outContent.toString().toLowerCase();

        assertTrue(output.contains("tue prenotazioni"),
            "L'output deve contenere la sezione delle prenotazioni");
        assertTrue(output.contains("martedi"),
            "L'output deve contenere il giorno della prenotazione");
        assertTrue(output.contains("vincenzo"),
            "L'output deve contenere il nome del parrucchiere");
    }
}
