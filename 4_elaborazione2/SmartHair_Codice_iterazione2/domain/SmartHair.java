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
    private List<Pulizia> elencoPulizie;
    private final Map<String, String> credenzialiAdmin;

    //Listino servizi
    private Map<String, Double> listinoPrezzi; // servizio -> prezzo

    private Cliente clienteCorrente;
    private Prenotazione prenotazioneCorrente;
    private Consiglio consiglioCorrente;
    private final List<String> giorniSenzaPulizia;
    private boolean adminLoggato=false;


    //Costruttore privato
    private SmartHair() {
        elencoClienti = new HashMap<>();
        elencoCredenziali=new HashMap<>();
        elencoTessere = new HashMap<>();
        elencoPrenotazioni = new ArrayList<>();
        mappaOrariDip = new HashMap<>();
        elencoParrucchieri = new ArrayList<>();
        credenzialiAdmin= new HashMap<>();
        elencoPulizie=new ArrayList<>();
        giorniSenzaPulizia= new ArrayList<>(Arrays.asList("martedi", "mercoledi", "giovedi", "venerdi", "sabato"));

        sistemaGiorniSenzaPulizia();
        inizializzaListino(); // inizializza i prezzi
        inizializzaParrucchieri();

        credenzialiAdmin.put("admin@gmail.com", "admin1234");
        clienteCorrente = null;
        prenotazioneCorrente = null;
        consiglioCorrente=null;
    }

    //Metodo per ottenere l'unica istanza
    public static SmartHair getInstance() {
        if (instance == null) {
            instance = new SmartHair();
        }
        return instance;
    }

    //CASO D'USO 4
    public boolean AccessoAdmin(String email, String password){
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
        boolean valid= verificaCredenzialiAdmin(email, password);
        
        if (valid) {
            adminLoggato=true;
            System.out.println(" Accesso riuscito per: " + email);
        } else {
            System.out.println(" Accesso negato per: " + email);
        }

        return valid;
    }

    private boolean verificaCredenzialiAdmin(String email, String password) {
        if (!credenzialiAdmin.containsKey(email))
            return false;

        String passwordSalvata = credenzialiAdmin.get(email);
        return passwordSalvata.equals(password);
    }

    public void logoutAdmin() {
        if (adminLoggato) {
            adminLoggato = false;
            System.out.println("Logout amministratore effettuato con successo.");
        } else {
            System.out.println("Nessun amministratore loggato.");
        }
    }

    public boolean isAdminLoggato() {
        return adminLoggato;
    }

    public List<String> recuperaGiorniSenzaPulizia(){
        if(giorniSenzaPulizia.isEmpty()){
            System.out.println("Tutti i giorni sono già stati assegnati per le pulizie.");
        }else{
            System.out.println("\n--- Giorni senza pulizia assegnata ---");
            for (String giorno : giorniSenzaPulizia) {
                System.out.println("- " + giorno);
            }
        }
        return giorniSenzaPulizia;
    }

    public boolean inserisciDati(String nomeDipendente, String giorno){
        // === Validazioni iniziali ===
        if (nomeDipendente == null || nomeDipendente.trim().isEmpty()) {
            System.out.println("Errore: il nome del dipendente non può essere vuoto.");
            return false;
        }
        if (giorno == null || giorno.trim().isEmpty()) {
            System.out.println("Errore: il giorno non può essere vuoto.");
            return false;
        }

        // Trova il parrucchiere
        Parrucchiere parrucchiere = null;
        for (Parrucchiere p : elencoParrucchieri) {
            if (p.getNome().equalsIgnoreCase(nomeDipendente)) {
                parrucchiere = p;
                break;
            }
        }

        if (parrucchiere == null) {
            System.out.println("Errore: parrucchiere non trovato (" + nomeDipendente + ")");
            return false;
        }

        //trova giorno 
        // === 2. Verifica che il giorno sia presente in giorniSenzaPulizia ===
        boolean giornoDisponibile = false;
        for (String g : giorniSenzaPulizia) {
            if (g.equalsIgnoreCase(giorno)) {
                giornoDisponibile = true;
                break;
            }
        }

        if (!giornoDisponibile) {
            System.out.println("Errore: il giorno '" + giorno + "' non è disponibile o è già stato assegnato.");
            return false;
        }
        
        Pulizia nuovaPulizia = new Pulizia(giorno, parrucchiere);
        System.out.println("\nCreata nuova istanza di Pulizia: " + nuovaPulizia);

        elencoPulizie.add(nuovaPulizia);
        System.out.println("Pulizia aggiunta a elencoPulizie.");

        giorniSenzaPulizia.remove(giorno);
        System.out.println("Giorno '" + giorno + "' rimosso da giorniSenzaPulizia.");
        return true;

    }

    public void visualizzaTurniPulizia() {

        if (elencoPulizie == null || elencoPulizie.isEmpty()) {
            System.out.println("Nessun turno di pulizia assegnato.");
            return;
        }

        System.out.println("\n--- TURNI DI PULIZIA ASSEGNATI ---");
        for (Pulizia p : elencoPulizie) {
            System.out.println("Giorno: " + p.getGiorno() +" | Parrucchiere assegnato: " + p.getAssegnato().getNome());
        }
    }

    // Caso d’uso UC5: Detrazione Credito
    public boolean detrazioneCredito(double valoreServizio) {
        //Controlli di validità di base
        if (valoreServizio <= 0) {
            System.out.println("Errore: l'importo da detrarre deve essere positivo.");
            return false;
        }

        if (clienteCorrente == null) {
            System.out.println("Errore: nessun cliente loggato. Effettua prima l'accesso.");
            return false;
        }

        //Recupera la tessera associata al cliente
        Tessera tessera = clienteCorrente.getTessera();
        if (tessera == null) {
            System.out.println("Errore: nessuna tessera associata al cliente " + clienteCorrente.getEmail());
            return false;
        }

        double creditoAttuale = tessera.getCredito();

        //Controlla che il credito sia sufficiente
        if (valoreServizio > creditoAttuale) {
            System.out.println("Credito insufficiente per detrarre " + valoreServizio + "$");
            System.out.println("Credito disponibile: " + creditoAttuale + "$");
            return false;
        }

        //Esegue l’aggiornamento saldo (by Information Expert)
        tessera.aggiornaSaldo(-valoreServizio); // detrazione del valore

        System.out.println("\n--- DETRAZIONE CREDITO ---");
        System.out.println("Cliente: " + clienteCorrente.getNome() + " " + clienteCorrente.getCognome());
        System.out.println("Servizio: valore " + valoreServizio + "$");
        System.out.println("Credito precedente: " + creditoAttuale + "$");
        System.out.println("Credito aggiornato: " + tessera.getCredito() + "$");
    
        return true;
    }

    // Caso d’uso UC5: Rimborso Credito
    public boolean rimborsoCredito(double valoreServizio) {
        //Controlli di base
        if (valoreServizio <= 0) {
        System.out.println("Errore: il valore del rimborso deve essere positivo.");
        return false;
        }

        if (clienteCorrente == null) {
            System.out.println("Errore: nessun cliente loggato. Effettua prima l'accesso.");
            return false;
        }

        //Recupera la tessera
        Tessera tessera = clienteCorrente.getTessera();
        if (tessera == null) {
            System.out.println("Errore: nessuna tessera associata al cliente " + clienteCorrente.getEmail());
            return false;
        }

        double creditoPrecedente = tessera.getCredito();

        //Aggiunge l'importo al credito
        tessera.aggiornaSaldo(valoreServizio);

        //Stampa informazioni
        System.out.println("\n--- RIMBORSO CREDITO ---");
        System.out.println("Cliente: " + clienteCorrente.getNome() + " " + clienteCorrente.getCognome());
        System.out.println("Valore rimborsato: " + valoreServizio + "$");
        System.out.println("Credito precedente: " + creditoPrecedente + "$");
        System.out.println("Credito aggiornato: " + tessera.getCredito() + "$");

        return true;
    }

    //SSD 6: GENERA CONSIGLIO
    public boolean presenzaOrariDisponibili() {
        if (mappaOrariDip == null || mappaOrariDip.isEmpty()) {
            return false;
        }

        // Scorre la struttura per assicurarsi che almeno un giorno contenga orari
        for (Map<String, List<String>> giorni : mappaOrariDip.values()) {
            for (List<String> orari : giorni.values()) {
                if (orari != null && !orari.isEmpty()) {
                    return true; // c’è almeno un orario disponibile
                }
            }
        }

        // Se arriva qui, significa che ogni lista è vuota
        return false;
    }

    public String controlloPresenzaPrenotazioniEPrimoConsiglio() {
        //Controlla che ci sia un cliente loggato
        if (clienteCorrente == null) {
            return null;
        }

        //Recupera le prenotazioni
        List<Prenotazione> prenotazioniCliente = clienteCorrente.getPrenotazioni();
        
        //Se ci sono prenotazioni (prenotazioni != null)
        if (prenotazioniCliente != null && !prenotazioniCliente.isEmpty()) {

            // --- Controllo credito tessera rispetto al prezzo massimo ---
            double creditoDisponibile = clienteCorrente.getTessera().getCredito();
            double prezzoMassimo = Collections.max(listinoPrezzi.values());

            //controllo che il credito sia minore del prezzo massimo tra i valori dei servizi offerti
            if (creditoDisponibile < prezzoMassimo) {
                return null;
            }

            //Se il cliente non ha ancora ricevuto un consiglio
            if (!clienteCorrente.isPrimoConsiglio()) {
                System.out.println("Il cliente non ha ancora ricevuto un consiglio. Generazione in corso...");

                // Genera il servizio
                String servizioGenerato= generaServizio();

                // Imposta che il cliente ha ora ricevuto un consiglio
                clienteCorrente.setPrimoConsiglio(true);

                System.out.println("Consiglio generato e assegnato al cliente " + clienteCorrente.getNome());
                return servizioGenerato;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public String generaServizio() {
        // Controlla che ci sia un cliente loggato
        if (clienteCorrente == null) {
            return null;
        }

        // Recupera la lista di prenotazioni (sappiamo che non è null e contiene almeno una prenotazione)
        List<Prenotazione> prenotazioniCliente = clienteCorrente.getPrenotazioni();

        // Ottiene l'ultima prenotazione effettuata
        Prenotazione ultimaPrenotazione = prenotazioniCliente.get(prenotazioniCliente.size() - 1);
        String ultimoServizio = ultimaPrenotazione.getServizio().toLowerCase();

        String servizioConsigliato=null;

        // Logica di suggerimento basata sull’ultimo servizio
        switch (ultimoServizio) {
            case "taglio":
                servizioConsigliato = "piega"; // dopo un taglio, una piega è il follow-up logico
                break;
            case "colore":
                servizioConsigliato = "piega"; // il colore spesso richiede una piega
                break;
            case "piega":
                // alterna tra taglio o colore in modo casuale
                servizioConsigliato = new Random().nextBoolean() ? "taglio" : "colore";
                break;
        }

        System.out.println("In base all'ultima prenotazione (" + ultimoServizio + "), consigliamo il servizio: " + servizioConsigliato);
        return servizioConsigliato;
    }

    public boolean generaConsiglio(String servizio) {
        // Controlla che ci sia un cliente loggato
        if (clienteCorrente == null) {
            System.out.println("Errore: nessun cliente loggato.");
            return false;
        }

        // Se il servizio è nullo o vuoto, interrompi
        if (servizio == null || servizio.trim().isEmpty()) {
            System.out.println("Errore: servizio non valido per la generazione del consiglio.");
            return false;
        }

        System.out.println("\n--- CONSIGLIO PERSONALIZZATO ---");
        System.out.println("Ti consigliamo di provare il servizio: " + servizio);
        System.out.print("Vuoi accettare questo consiglio? (si/no): ");

        Scanner scanner = new Scanner(System.in);
        String risposta = scanner.nextLine().trim().toLowerCase();

        boolean consiglioAccettato = risposta.equals("s") || risposta.equals("si");

        // Se il consiglio è accettato
        if (consiglioAccettato) {
            consiglioCorrente = new Consiglio();
            consiglioCorrente.setServizio(servizio);
            clienteCorrente.setConsiglio(consiglioCorrente);
            System.out.println("Consiglio accettato e salvato con successo!");
            return true;
        } else {
            System.out.println("Consiglio rifiutato. Nessuna modifica effettuata.");
            return false;
        }
    }

    public Map<String, Map<String, List<String>>> prelevaOrariDisponibili() {
        // Controlla che ci siano orari disponibili
        if (mappaOrariDip == null || mappaOrariDip.isEmpty()) {
            System.out.println("Nessun orario disponibile al momento.");
            return null;
        }

        System.out.println("\n--- ORARI DISPONIBILI ---");

        // Ordine logico dei giorni
        List<String> ordineGiorni = Arrays.asList("martedi", "mercoledi", "giovedi", "venerdi", "sabato");

        // Itera per ogni dipendente e mostra i giorni e orari
        for (String dipendente : mappaOrariDip.keySet()) {
            System.out.println("Dipendente: " + dipendente);
            Map<String, List<String>> giorni = mappaOrariDip.get(dipendente);
            for (String giorno : ordineGiorni) {
                if (giorni.containsKey(giorno)) {
                    System.out.println(" Giorno: " + giorno + " - Orari: " + giorni.get(giorno));
                }
            }
        }

        // Restituisce la struttura con tutti gli orari disponibili
        return mappaOrariDip;
    }
    
    public boolean aggiungiPrenotazione(String giorno, String ora, String nomeDip) {
        // === 1. Controlli preliminari ===
        if (clienteCorrente == null) {
            System.out.println("Errore: nessun cliente loggato.");
            return false;
        }

        if (giorno == null || ora == null || nomeDip == null ||
            giorno.trim().isEmpty() || ora.trim().isEmpty() || nomeDip.trim().isEmpty()) {
            System.out.println("Errore: dati non validi per la prenotazione.");
            return false;
        }

        // === 2. Recupera l'ultimo consiglio accettato ===
        if (clienteCorrente.getConsiglio() == null) {
            System.out.println("Errore: nessun consiglio disponibile. Genera prima un consiglio.");
            return false;
        }

        String servizio = clienteCorrente.getConsiglio().getServizio();

        // Controlla che il dipendente esista nella mappa
        if (!mappaOrariDip.containsKey(nomeDip)) {
            System.out.println(" Errore: dipendente non trovato (" + nomeDip + ")");
            return false;
        }

        Map<String, List<String>> orariDip = mappaOrariDip.get(nomeDip);

        // Controlla che il giorno esista
        if (!orariDip.containsKey(giorno)) {
            System.out.println(" Errore: giorno non valido (" + giorno + ")");
            return false;
        }

        List<String> orari = orariDip.get(giorno);

        // Verifica se l’orario scelto è disponibile
        if (!orari.contains(ora)) {
            System.out.println(" Orario non disponibile per " + nomeDip + " il " + giorno + " alle " + ora);
            return false;
        }

        // === 3. Trova il parrucchiere corrispondente ===
        Parrucchiere parrucchiere = null;
        for (Parrucchiere p : elencoParrucchieri) {
            if (p.getNome().equalsIgnoreCase(nomeDip)) {
                parrucchiere = p;
                break;
            }
        }

        if (parrucchiere == null) {
            System.out.println("Errore: parrucchiere non trovato (" + nomeDip + ")");
            return false;
        }

        // === 4. Crea la nuova prenotazione ===
        double prezzoServizio = listinoPrezzi.getOrDefault(servizio.toLowerCase(), 0.0);
        Prenotazione nuovaPrenotazione = new Prenotazione(ora, giorno, servizio, clienteCorrente, parrucchiere, prezzoServizio);
        prenotazioneCorrente = nuovaPrenotazione;

        detrazioneCredito(prezzoServizio);

        // === 5. Rimuove l’orario selezionato dalla mappa ===
        rimuoviOrarioDisponibile(giorno, ora, nomeDip);

        // === 6. Aggiunge la prenotazione alla lista globale e a quella del cliente ===
        elencoPrenotazioni.add(nuovaPrenotazione);
        clienteCorrente.aggiungiPrenotazione(nuovaPrenotazione);

        return true;
    }

    
    private void inizializzaParrucchieri() {

        // Creiamo i due dipendenti
        Parrucchiere vincenzo = new Parrucchiere("vincenzo");
        Parrucchiere jose = new Parrucchiere("jose");

        elencoParrucchieri.add(vincenzo);
        elencoParrucchieri.add(jose);

        // Giorni lavorativi totali (martedi → sabato)
        List<String> giorniSettimana = Arrays.asList("martedi", "mercoledi", "giovedi", "venerdi", "sabato");
        // Orari
        List<String> orari = Arrays.asList("9", "11", "15", "17");

        // === Calcolo giorni da inserire in base al giorno corrente ===
        Calendar calendar = Calendar.getInstance();
        int oggi = calendar.get(Calendar.DAY_OF_WEEK); // 1 = Domenica, 2 = Lunedì, ..., 7 = Sabato

        // Determina il giorno corrente
        String giornoCorrente = switch (oggi) {
            case Calendar.TUESDAY -> "martedi";
            case Calendar.WEDNESDAY -> "mercoledi";
            case Calendar.THURSDAY -> "giovedi";
            case Calendar.FRIDAY -> "venerdi";
            case Calendar.SATURDAY -> "sabato";
            default -> "martedi"; // Se è domenica o lunedì, mostra tutti da martedì
        };

        // Trova indice di inizio
        int indiceInizio = giorniSettimana.indexOf(giornoCorrente);

        // Costruisce la lista dei giorni validi da oggi a sabato
        List<String> giorniValidi = giorniSettimana.subList(indiceInizio, giorniSettimana.size());

        // === Popola la mappa solo con i giorni validi ===
        for (Parrucchiere d : elencoParrucchieri) {
            Map<String, List<String>> orariGiornalieri = new HashMap<>();
            for (String giorno : giorniValidi) {
                orariGiornalieri.put(giorno, new ArrayList<>(orari));
            }
            mappaOrariDip.put(d.getNome(), orariGiornalieri);
        }
    }

    private void inizializzaListino() {
        listinoPrezzi = new HashMap<>();
        listinoPrezzi.put("taglio", 20.0);
        listinoPrezzi.put("colore", 40.0);
        listinoPrezzi.put("piega", 10.0);
    }

    private void sistemaGiorniSenzaPulizia(){
        // === Controllo: rimuove i giorni antecedenti al giorno corrente ===
        Calendar calendar = Calendar.getInstance();
        int oggi = calendar.get(Calendar.DAY_OF_WEEK); // 1 = Domenica, 2 = Lunedì, ..., 7 = Sabato

        List<String> ordine = Arrays.asList("martedi", "mercoledi", "giovedi", "venerdi", "sabato");

        String giornoCorrente = switch (oggi) {
            case Calendar.TUESDAY -> "martedi";
            case Calendar.WEDNESDAY -> "mercoledi";
            case Calendar.THURSDAY -> "giovedi";
            case Calendar.FRIDAY -> "venerdi";
            case Calendar.SATURDAY -> "sabato";
            default -> "martedi"; // se è domenica o lunedì, tiene tutta la settimana
        };

        int indiceOggi = ordine.indexOf(giornoCorrente);

        // Mantiene solo i giorni da oggi fino a sabato
        giorniSenzaPulizia.retainAll(ordine.subList(indiceOggi, ordine.size()));
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

        //Recupera il prezzo dal listino in base al servizio selezionato
        double prezzoServizio = 0.0;
        if (listinoPrezzi.containsKey(servizio.toLowerCase())) {
            prezzoServizio = listinoPrezzi.get(servizio.toLowerCase());
        } else {
            System.out.println("Servizio non trovato nel listino, prezzo impostato a 0.0");
        }

        // Creazione della nuova prenotazione (by Creator)
        prenotazioneCorrente = new Prenotazione(orario, giorno, servizio, clienteCorrente,parrucchiere, prezzoServizio);

        // Aggiunta alla lista prenotazioni (by Information Expert)
        elencoPrenotazioni.add(prenotazioneCorrente);
        clienteCorrente.aggiungiPrenotazione(prenotazioneCorrente);

        // Messaggio di conferma (ritorno a Controller)
        System.out.println("Prenotazione confermata!");
        System.out.println("Giorno: " + giorno);
        System.out.println("Orario: " + orario);
        System.out.println("Parrucchiere: " + nomeDip);
        System.out.println(" Servizio: " + servizio);
        System.out.println("Prezzo: " + prezzoServizio + "$");
        System.out.println("Cliente: " + clienteCorrente.getEmail());

        detrazioneCredito(prezzoServizio);
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

    public boolean stampaPrenotazioniCliente() {
        if (clienteCorrente == null) {
            System.out.println("Nessun cliente loggato. Effettua prima l'accesso.");
            return false;
        }

        List<Prenotazione> lista = clienteCorrente.getPrenotazioni();

        if (lista.isEmpty()) {
            System.out.println("Nessuna prenotazione trovata per " + clienteCorrente.getNome() + ".");
            return false;
        }

        // === Determina il giorno corrente ===
        Calendar calendar = Calendar.getInstance();
        int oggi = calendar.get(Calendar.DAY_OF_WEEK); // 1 = Domenica, ..., 7 = Sabato

        // Mappa dei giorni lavorativi ordinati
        List<String> giorniSettimana = Arrays.asList("martedi", "mercoledi", "giovedi", "venerdi", "sabato");

        // Traduzione del giorno corrente
        String giornoCorrente = switch (oggi) {
            case Calendar.TUESDAY -> "martedi";
            case Calendar.WEDNESDAY -> "mercoledi";
            case Calendar.THURSDAY -> "giovedi";
            case Calendar.FRIDAY -> "venerdi";
            case Calendar.SATURDAY -> "sabato";
            default -> "martedi"; // se è domenica o lunedì, parte da martedì
        };

        // Indice del giorno corrente
        int indiceOggi = giorniSettimana.indexOf(giornoCorrente);

        // === Filtra le prenotazioni: rimuovi quelle antecedenti ===
        lista.removeIf(p -> {
            String giornoPrenotazione = p.getGiorno().toLowerCase();
            int indicePrenotazione = giorniSettimana.indexOf(giornoPrenotazione);
            // Se il giorno non è riconosciuto o è prima di oggi → rimuovi
            return indicePrenotazione < indiceOggi;
        });

        // === Stampa il risultato ===
        if (lista.isEmpty()) {
            System.out.println("Non ci sono prenotazioni future per " + clienteCorrente.getNome() + ".");
            return false;
        } else {
            System.out.println("\n--- LE TUE PRENOTAZIONI ---");
            for (Prenotazione p : lista) {
                System.out.println("Codice: " + p.getCodice() +
                    " | Giorno: " + p.getGiorno() +
                    " | Ora: " + p.getOrario() +
                    " | Parrucchiere: " + p.getParrucchiere().getNome() +
                    " | Servizio: " + p.getServizio());
            }
            return true;
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

        rimborsoCredito(daRimuovere.getPrezzoServizio());

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
        clienteCorrente.setPrimoConsiglio(false);
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

    // Mostra il credito residuo della tessera del cliente corrente
    public void visualizzaCreditoCliente() {
        if (clienteCorrente == null) {
            System.out.println("Errore: nessun cliente loggato. Effettua prima l'accesso.");
            return;
        }

        Tessera tessera = clienteCorrente.getTessera();
        if (tessera == null) {
            System.out.println("Errore: nessuna tessera associata al cliente " + clienteCorrente.getEmail());
            return;
        }

        System.out.println("\n--- CREDITO TESSERA ---");
        System.out.println("Cliente: " + clienteCorrente.getNome() + " " + clienteCorrente.getCognome());
        System.out.println("Credito disponibile: " + tessera.getCredito() + "$");
    }

    // Mostra l'elenco completo dei parrucchieri (solo per admin)
    public void visualizzaElencoParrucchieri() {
        if (!adminLoggato) {
            System.out.println("Errore: accesso negato. Solo l'amministratore può visualizzare i parrucchieri.");
            return;
        }

        if (elencoParrucchieri.isEmpty()) {
            System.out.println("Nessun parrucchiere registrato nel sistema.");
            return;
        }

        System.out.println("\n--- ELENCO PARRUCCHIERI ---");
        for (Parrucchiere p : elencoParrucchieri) {
            System.out.println("- " + p.getNome());
        }
    }
}