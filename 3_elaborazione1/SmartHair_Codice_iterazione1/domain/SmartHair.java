package domain;

import java.util.*;

public class SmartHair {

    // Singleton: unica istanza
    private static SmartHair instance;

    //Strutture dati principali
    private Map<String, Cliente> elencoClienti;//string è l'email
    private Map<String, String> elencoCredenziali;//email e password
    private Map<String,Tessera> elencoTessere;//email e tessera
    private List<Prenotazione> elencoPrenotazioni;
    private Map<String, Map<String, List<String>>> mappaOrariDip;//nome parrucchiere-> giorno lista orari
    private List<Parrucchiere> elencoParrucchieri;
    //Listino servizi
    private Map<String, Integer> listinoPrezzi; // servizio -> prezzo

    private Cliente clienteCorrente;
    private Prenotazione prenotazioneCorrente;


    //Costruttore privato
    private SmartHair() {
        elencoClienti = new HashMap<>();
        elencoCredenziali=new HashMap<>();
        elencoTessere = new HashMap<>();
        elencoPrenotazioni = new ArrayList<>();
        mappaOrariDip = new HashMap<>();
        elencoParrucchieri = new ArrayList<>();

        inizializzaListino(); // inizializza i prezzi
        inizializzaParrucchieri();

        clienteCorrente = null;
        prenotazioneCorrente = null;
    }

    //Metodo per ottenere l'unica istanza
    public static SmartHair getInstance() {
        if (instance == null) {
            instance = new SmartHair();
        }
        return instance;
    }

    private void inizializzaParrucchieri() {

        // Creiamo i due dipendenti
        Parrucchiere vincenzo = new Parrucchiere("vincenzo");
        Parrucchiere jose = new Parrucchiere("jose");

        elencoParrucchieri.add(vincenzo);
        elencoParrucchieri.add(jose);

        // Giorni lavorativi
        List<String> giorni = Arrays.asList("martedi", "mercoledi", "giovedi", "venerdi", "sabato");
        //  Orari
        List<String> orari = Arrays.asList("9", "11", "15", "17");

        // Crea la struttura centralizzata
        for (Parrucchiere d : elencoParrucchieri) {
            Map<String, List<String>> orariGiornalieri = new HashMap<>();
            for (String giorno : giorni) {
                // Ogni giorno ha la propria lista di orari
                orariGiornalieri.put(giorno, new ArrayList<>(orari));
            }
            mappaOrariDip.put(d.getNome(), orariGiornalieri);
        }
    }

    private void inizializzaListino() {
        listinoPrezzi = new HashMap<>();
        listinoPrezzi.put("taglio", 20);
        listinoPrezzi.put("colore", 40);
        listinoPrezzi.put("piega", 10);
    }

    private String generaHash(String password) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            // Convertiamo i byte in formato esadecimale leggibile
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Errore nella generazione dell'hash", e);
        }
    }

    // Controlla che la password sia valida
    private boolean validaPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        return true;
    }

    // Controlla che l'email sia nel formato corretto
    private boolean validaEmail(String email) {
        if (email == null) return false;
        // espressione regolare semplice per email standard
        return email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$");
    }

    //Crea nuovo cliente e tessera
    public Cliente nuovoCliente(String nome, String cognome, String email, String password) {

        // Controllo dati anagrafici
        if (nome == null || nome.trim().isEmpty()) {
            System.out.println("Errore: il nome non può essere vuoto.");
            return null;
        }
        if (cognome == null || cognome.trim().isEmpty()) {
            System.out.println("Errore: il cognome non può essere vuoto.");
            return null;
        }

        // Controllo formato email
        if (!validaEmail(email)) {
            System.out.println("Errore: email non valida. Deve contenere '@' e un dominio (.com, .it, ...)");
            return null;
        }

        //Controllo robustezza password
        if (!validaPassword(password)) {
            System.out.println("Errore: la password deve avere almeno 8 caratteri.");
            return null;
        }

        // Verifica duplicati
        if (elencoClienti.containsKey(email)) {
            System.out.println("Errore: cliente già registrato!");
            return null;
        }

        // <<create>> Cliente
        Cliente nuovoCliente = new Cliente(nome, cognome, email);

        //<<add>> credenziali (Information Expert)
        String hashPassword = generaHash(password);
        elencoClienti.put(email, nuovoCliente);
        elencoCredenziali.put(email, hashPassword);

        // <<add>> Tessera all’elenco
        elencoTessere.put(email, nuovoCliente.getTessera());

        System.out.println(" Nuovo cliente creato con successo!\n");
        return nuovoCliente;
    }

    //Verifica accesso
    public boolean accesso(String email, String password) {
        // Validazione base
        if (email == null || email.trim().isEmpty()) {
            System.out.println("Errore: l'email non può essere vuota.");
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            System.out.println("Errore: la password non può essere vuota.");
            return false;
        }

        if (!validaEmail(email)) {
            System.out.println("Errore: formato email non valido.");
            return false;
        }

        // <<information expert>> Verifica credenziali
        boolean valid = verificaCredenziali(email, password);

        if (valid) {
            clienteCorrente = elencoClienti.get(email);
            System.out.println(" Accesso riuscito per: " + email);
            System.out.println(" Benvenuto, " + clienteCorrente.getNome() + " " + clienteCorrente.getCognome() + "!");
        } else {
            System.out.println(" Accesso negato per: " + email);
            clienteCorrente = null;
        }

        return valid;
    }

    private boolean verificaCredenziali(String email, String password) {
        if (!elencoCredenziali.containsKey(email))
            return false;

        String hashInserito = generaHash(password);
        String hashSalvato = elencoCredenziali.get(email);

        return hashInserito.equals(hashSalvato);
    }


    //Seleziona servizio: verifica tessera, credito e mostra orari disponibili
    public Map<String, Map<String, List<String>>> selezionaServizio(String servizio) {
        //Controlla che il servizio esista nel listino
        if (!listinoPrezzi.containsKey(servizio.toLowerCase())) {
            System.out.println("Servizio non valido. Servizi disponibili: " + listinoPrezzi.keySet());
            return null;
        }

        // Verifica che ci sia un cliente loggato
        if (clienteCorrente == null) {
            System.out.println("Errore: nessun cliente attualmente loggato.");
            return null;
        }

        //recupero la tessera del cliente corrente
        Tessera tessera = clienteCorrente.getTessera();
        if (tessera == null) {
            System.out.println("Errore: il cliente non possiede una tessera associata.");
            return null;
        }

        //Recupera il prezzo corrispondente
        double prezzoServizio = listinoPrezzi.get(servizio.toLowerCase());
        System.out.println("Prezzo del servizio '" + servizio + "': " + prezzoServizio + "$");

        //Verifica credito sufficiente
        if (tessera.getCredito()<prezzoServizio) {
            System.out.println("Credito insufficiente per il servizio: " + servizio);
            return null;
        }

        // Mostra orari disponibili
        if (mappaOrariDip.isEmpty()) {
            System.out.println("Nessun orario disponibile al momento.");
            return null;
        }

        System.out.println("\n Orari disponibili per il servizio '" + servizio + "':");
        
        // Ordine logico dei giorni
        List<String> ordineGiorni = Arrays.asList("martedi", "mercoledi", "giovedi", "venerdi", "sabato");
        
        for (String dipendente : mappaOrariDip.keySet()) {
            System.out.println("Dipendente: " + dipendente);
            Map<String, List<String>> giorni = mappaOrariDip.get(dipendente);
            for (String giorno : ordineGiorni) {
                if(giorni.containsKey(giorno)){
                    System.out.println(" Giorno: " + giorno + " - Orari: " + giorni.get(giorno));
                }    
            }
        }

        return mappaOrariDip;
    }

    public String selezionaOrario(String giorno, String ora, String nomeDipendente) {
        // Controlla che il dipendente esista nella mappa
        if (!mappaOrariDip.containsKey(nomeDipendente)) {
            System.out.println(" Errore: dipendente non trovato (" + nomeDipendente + ")");
            return null;
        }

        Map<String, List<String>> orariDip = mappaOrariDip.get(nomeDipendente);

        // Controlla che il giorno esista
        if (!orariDip.containsKey(giorno)) {
            System.out.println(" Errore: giorno non valido (" + giorno + ")");
            return null;
        }

        List<String> orari = orariDip.get(giorno);

        // Verifica se l’orario scelto è disponibile
        if (!orari.contains(ora)) {
            System.out.println(" Orario non disponibile per " + nomeDipendente + " il " + giorno + " alle " + ora);
            return null;
        }

        // Rimuove l’orario (come nel diagramma)
        rimuoviOrarioDisponibile(giorno, ora, nomeDipendente);

        System.out.println(" Orario selezionato con successo: " + giorno + " " + ora + " con " + nomeDipendente);
        return ora; // ritorna l'orario selezionato per conferma
    }
    
    private void rimuoviOrarioDisponibile(String giorno, String ora, String nomeDipendente) {
        if (mappaOrariDip.containsKey(nomeDipendente) && mappaOrariDip.get(nomeDipendente).containsKey(giorno)) {
            mappaOrariDip.get(nomeDipendente).get(giorno).remove(ora);
            System.out.println("Rimosso direttamente da mappaOrariDip -> "
                + nomeDipendente + " " + giorno + " " + ora);
        }
    }
    
    public void confermaOrario(String orario, String giorno, String nomeDip, String servizio) {
        //Validazioni di base
        if (orario == null || giorno == null || nomeDip == null || servizio == null) {
            System.out.println(" Errore: dati mancanti per la conferma della prenotazione.");
            return;
        }

        // Verifica che un cliente sia loggato
        if (clienteCorrente == null) {
            System.out.println("Errore: nessun cliente attualmente loggato.");
            return;
        }

        // Trova il parrucchiere
        Parrucchiere parrucchiere = null;
        for (Parrucchiere p : elencoParrucchieri) {
            if (p.getNome().equalsIgnoreCase(nomeDip)) {
                parrucchiere = p;
                break;
            }
        }

        if (parrucchiere == null) {
            System.out.println("Errore: parrucchiere non trovato (" + nomeDip + ")");
            return;
        }

        // Creazione della nuova prenotazione (by Creator)
        prenotazioneCorrente = new Prenotazione(orario, giorno, servizio, clienteCorrente,parrucchiere);

        // Aggiunta alla lista prenotazioni (by Information Expert)
        elencoPrenotazioni.add(prenotazioneCorrente);
        clienteCorrente.aggiungiPrenotazione(prenotazioneCorrente);

        // Messaggio di conferma (ritorno a Controller)
        System.out.println("Prenotazione confermata!");
        System.out.println("Giorno: " + giorno);
        System.out.println("Orario: " + orario);
        System.out.println("Parrucchiere: " + nomeDip);
        System.out.println(" Servizio: " + servizio);
        System.out.println("Cliente: " + clienteCorrente.getEmail());
    }

    // --- VISUALIZZA PRENOTAZIONI ---
    public List<Prenotazione> getPrenotazioniCliente() {
        if (clienteCorrente == null) {
            System.out.println("Nessun cliente loggato. Effettua prima l'accesso.");
            return Collections.emptyList();
        }

        // Recupera direttamente la lista dal cliente corrente
        return clienteCorrente.getPrenotazioni();
    }

    public void stampaPrenotazioniCliente() {
        if (clienteCorrente == null) {
            System.out.println("Nessun cliente loggato. Effettua prima l'accesso.");
            return;
        }

        List<Prenotazione> lista = clienteCorrente.getPrenotazioni();

        if (lista.isEmpty()) {
            System.out.println("Nessuna prenotazione trovata per " + clienteCorrente.getNome() + ".");
            return;
        }

        System.out.println("\n--- LE TUE PRENOTAZIONI ---");
        for (Prenotazione p : lista) {
            System.out.println("Codice: " + p.getCodice() +
                    " | Giorno: " + p.getGiorno() +
                    " | Ora: " + p.getOrario() +
                    " | Parrucchiere: " + p.getParrucchiere().getNome() +
                    " | Servizio: " + p.getServizio());
        }
    }

    // --- CANCELLA PRENOTAZIONE ---
    public boolean cancellaPrenotazione(int codice) {
        if (clienteCorrente == null) {
            System.out.println("Nessun cliente loggato. Effettua prima l'accesso.");
            return false;
        }

        Prenotazione daRimuovere = null;

        // Cerca tra le prenotazioni del cliente corrente
        for (Prenotazione p : clienteCorrente.getPrenotazioni()) {
            if (p.getCodice() == codice) {
                daRimuovere = p;
                break;
            }
        }

        if (daRimuovere == null) {
            System.out.println("Prenotazione non trovata per il cliente corrente.");
            return false;
        }

        // Rimuove sia dal cliente che dalla lista globale
        clienteCorrente.rimuoviPrenotazione(daRimuovere);
        elencoPrenotazioni.remove(daRimuovere);

        // Ripristina orario disponibile
        String nomeParrucchiere = daRimuovere.getParrucchiere().getNome();
        ripristinaOrarioDisponibile(nomeParrucchiere, daRimuovere.getGiorno(), daRimuovere.getOrario());

        System.out.println("Prenotazione " + codice + " cancellata con successo.");
        return true;
    }

    public void logout() {
        // Se non c'è alcun cliente loggato
        if (clienteCorrente == null) {
            System.out.println(" Nessun cliente attualmente loggato.");
            return;
        }

        System.out.println("\n Logout effettuato con successo.");
        System.out.println("Arrivederci, " + clienteCorrente.getNome() + " " + clienteCorrente.getCognome() + "!");

        // Azzera i riferimenti alla sessione corrente
        clienteCorrente = null;
        prenotazioneCorrente = null;
    }

    private void ripristinaOrarioDisponibile(String dip, String giorno, String ora) {
        if (!mappaOrariDip.containsKey(dip)) return;
        // Recupera direttamente la lista in mappaOrariDip e aggiorna
        if (!mappaOrariDip.get(dip).get(giorno).contains(ora)) {
            mappaOrariDip.get(dip).get(giorno).add(ora);
            mappaOrariDip.get(dip).get(giorno).sort(Comparator.comparingInt(Integer::parseInt));
        }

        System.out.println("Orario ripristinato in mappaOrariDip -> " + dip + " " + giorno + " " + ora);
    }

    // Caso d’uso UC3: Ricarica Tessera
    public boolean ricaricaTessera(double valore) {

        //Controlli di validità
        if (valore <= 0) {
            System.out.println("Errore: l'importo della ricarica deve essere maggiore di zero.");
            return false;
        }

        // Verifica che ci sia un cliente loggato
        if (clienteCorrente == null) {
            System.out.println("Errore: nessun cliente loggato. Effettua prima l'accesso.");
            return false;
        }

        // Recupera la tessera del cliente corrente
        Tessera tessera = clienteCorrente.getTessera();

        if (tessera == null) {
            System.out.println("Errore: nessuna tessera associata al cliente " + clienteCorrente.getEmail());
            return false;
        }

        // Effettua la ricarica (Information Expert)
        double creditoPrecedente = tessera.getCredito();
        tessera.setCredito(creditoPrecedente + valore);

        // Stampa conferma ricarica
        System.out.println("\n Ricarica effettuata con successo!");
        System.out.println("Cliente: " + clienteCorrente.getNome() + " " + clienteCorrente.getCognome());
        System.out.println("Email: " + clienteCorrente.getEmail());
        System.out.println("Valore ricaricato: " + valore + "$");
        System.out.println("Credito precedente: " + creditoPrecedente + "$");
        System.out.println("Nuovo credito: " + tessera.getCredito() + "$");

        return true;
    }

    // === Getter per il cliente corrente ===
    public Cliente getClienteCorrente() {
        return clienteCorrente;
    }

    // === Getter per la prenotazione corrente (opzionale) ===
    public Prenotazione getPrenotazioneCorrente() {
        return prenotazioneCorrente;
    }

}

