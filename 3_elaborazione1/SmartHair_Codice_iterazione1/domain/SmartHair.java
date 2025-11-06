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
    private Map<String, Map<String, List<String>>> mappaOrariDip;
    private List<Parrucchiere> elencoParrucchieri;
    //Listino servizi
    private Map<String, Integer> listinoPrezzi;


    //Costruttore privato
    private SmartHair() {
        elencoClienti = new HashMap<>();
        elencoCredenziali=new HashMap<>();
        elencoTessere = new HashMap<>();
        elencoPrenotazioni = new ArrayList<>();
        mappaOrariDip = new HashMap<>();
        inizializzaListino(); // inizializza i prezzi
        inizializzaParrucchieri();
    }

    //Metodo per ottenere l'unica istanza
    public static SmartHair getInstance() {
        if (instance == null) {
            instance = new SmartHair();
        }
        return instance;
    }

    private void inizializzaParrucchieri() {
        elencoParrucchieri = new ArrayList<>();

        // Creiamo i due dipendenti
        Parrucchiere vincenzo = new Parrucchiere("vincenzo");
        Parrucchiere jose = new Parrucchiere("jose");

        // Giorni lavorativi
        List<String> giorni = Arrays.asList("martedi", "mercoledi", "giovedi", "venerdi", "sabato");
        //  Orari
        List<String> orari = Arrays.asList("9", "11", "15", "17");

        // Assegniamo gli orari a ogni dipendente
        for (Parrucchiere d : Arrays.asList(vincenzo, jose)) {
            Map<String, List<String>> orariGiornalieri = new HashMap<>();
            for (String giorno : giorni) {
                // Ogni giorno ha una nuova copia della lista orari
                orariGiornalieri.put(giorno, new ArrayList<>(orari));
            }
            d.setOrariDisponibili(orariGiornalieri);
            elencoParrucchieri.add(d);
            mappaOrariDip.put(d.getNome(), d.getOrariDisponibili());
        }

        System.out.println("Parrucchieri inizializzati con i rispettivi orari: Vincenzo e Jose.");
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
        System.out.println("- [Controller] SmartHair.nuovoCliente(" + nome + ", " + cognome + ", " + email + ")");

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
        if (elencoCredenziali.containsKey(email)) {
            System.out.println("Errore: cliente già registrato!");
            return null;
        }

        // <<create>> Cliente
        Cliente cliente = new Cliente(nome, cognome, email);
        System.out.println("- [Creator] Creato nuovo Cliente: " + cliente);

        //<<add>> credenziali (Information Expert)
        String hashPassword = generaHash(password);
        elencoCredenziali.put(email, hashPassword);
        System.out.println("- [Information Expert] Credenziali salvate per: " + email);

        // <<create>> Tessera
        Tessera tessera = new Tessera(email);
        System.out.println("-[Creator] Generata Tessera per " + email);

        // <<add>> Tessera all’elenco
        elencoTessere.put(email, tessera);
        elencoClienti.put(email, cliente);

        System.out.println(" Nuovo cliente creato con successo!\n");
        return cliente;
    }

    //Verifica accesso
    public boolean accesso(String email, String password) {
        System.out.println("- [Controller] SmartHair.accesso(" + email + ")");

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
            System.out.println(" Accesso riuscito per: " + email);
        } else {
            System.out.println(" Accesso negato per: " + email);
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
    public Map<String, Map<String, List<String>>> selezionaServizio(String email, String servizio) {
        //Controlla che il servizio esista nel listino
        if (!listinoPrezzi.containsKey(servizio.toLowerCase())) {
            System.out.println("Servizio non valido. Servizi disponibili: " + listinoPrezzi.keySet());
            return null;
        }

        //Recupera il prezzo corrispondente
        double prezzoServizio = listinoPrezzi.get(servizio.toLowerCase());
        System.out.println("Prezzo del servizio '" + servizio + "': " + prezzoServizio + "$");

        //Controlla che la tessera esista
        Tessera tessera = esisteTessera(email);
        if (tessera == null) {
            System.out.println("Nessuna tessera associata all'email: " + email);
            return null;
        }

        //Verifica credito sufficiente
        if (!verificaCredito(email, prezzoServizio)) {
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

    public Tessera esisteTessera(String email) {
        return elencoTessere.getOrDefault(email, null);
    }

    public boolean verificaCredito(String email, double prezzoServizio) {
        Tessera t = esisteTessera(email);
        if (t == null) return false;
        return t.getCredito() >= prezzoServizio;
    }

    public Map<String, Integer> getListinoPrezzi() {
        return listinoPrezzi;
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
        if (mappaOrariDip.containsKey(nomeDipendente)) {
            Map<String, List<String>> orariPerGiorno = mappaOrariDip.get(nomeDipendente);
            if (orariPerGiorno.containsKey(giorno)) {
                orariPerGiorno.get(giorno).remove(ora);
            }
        }
    }
    
    public void confermaOrario(String orario, String giorno, String nomeDip, String email, String servizio) {
        //Validazioni di base
        if (orario == null || giorno == null || nomeDip == null || email == null || servizio == null) {
            System.out.println(" Errore: dati mancanti per la conferma della prenotazione.");
            return;
        }

        // Verifica che il cliente esista
        if (!elencoClienti.containsKey(email)) {
            System.out.println(" Errore: cliente non registrato.");
            return;
        }

        // Creazione della nuova prenotazione (by Creator)
        Prenotazione nuovaPrenotazione = new Prenotazione(orario, giorno, nomeDip, email, servizio);

        // Aggiunta alla lista prenotazioni (by Information Expert)
        elencoPrenotazioni.add(nuovaPrenotazione);

        // Messaggio di conferma (ritorno a Controller)
        System.out.println("Prenotazione confermata!");
        System.out.println("Giorno: " + giorno);
        System.out.println("Orario: " + orario);
        System.out.println("Parrucchiere: " + nomeDip);
        System.out.println(" Servizio: " + servizio);
        System.out.println(" Cliente: " + email);
    }

    // --- VISUALIZZA PRENOTAZIONI ---
    public List<Prenotazione> getPrenotazioniCliente(String email) {
        List<Prenotazione> ris = new ArrayList<>();
        for (Prenotazione p : elencoPrenotazioni)
            if (p.getEmail().equalsIgnoreCase(email)) ris.add(p);
        return ris;
    }

    public void stampaPrenotazioniCliente(String email) {
        List<Prenotazione> lista = getPrenotazioniCliente(email);
        if (lista.isEmpty()) {
            System.out.println("Nessuna prenotazione trovata.");
            return;
        }
        System.out.println("\n--- LE TUE PRENOTAZIONI ---");
        for (Prenotazione p : lista) {
            System.out.println("Codice: " + p.getCodice() + " | Giorno: " + p.getGiorno()
                    + " | Ora: " + p.getOrario() + " | Dipendente: " + p.getDipendente()
                    + " | Servizio: " + p.getServizio());
        }
    }

    // --- CANCELLA PRENOTAZIONE ---
    public boolean cancellaPrenotazione(int codice, String email) {
        Prenotazione daRimuovere = null;
        for (Prenotazione p : elencoPrenotazioni){
            if (p.getCodice() == codice){
                daRimuovere = p;
                break;
            }
        }       

        if (daRimuovere == null) {
            System.out.println("Prenotazione non trovata.");
            return false;
        }

        elencoPrenotazioni.remove(daRimuovere);
        ripristinaOrarioDisponibile(daRimuovere.getDipendente(), daRimuovere.getGiorno(), daRimuovere.getOrario());
        System.out.println("Prenotazione " + codice + " cancellata con successo.");
        return true;
    }

    private void ripristinaOrarioDisponibile(String dip, String giorno, String ora) {
        if (!mappaOrariDip.containsKey(dip)) return;
        Map<String, List<String>> orariGiorno = mappaOrariDip.get(dip);
        List<String> lista = orariGiorno.get(giorno);
        if (lista == null) {
            lista = new ArrayList<>();
            orariGiorno.put(giorno, lista);
        }
        if (!lista.contains(ora)) {
            lista.add(ora);
            lista.sort(Comparator.comparingInt(Integer::parseInt));
        }
    }

    // Caso d’uso UC3: Ricarica Tessera
    public boolean ricaricaTessera(double valore, String email) {
        System.out.println("- [Controller] SmartHair.ricaricaTessera(" + valore + ", " + email + ")");

        //Controlli di validità
        if (valore <= 0) {
            System.out.println("Errore: l’importo della ricarica deve essere maggiore di zero.");
            return false;
        }

        if (email == null || email.trim().isEmpty()) {
            System.out.println("Errore: email non valida.");
            return false;
        }

        // Trova la tessera associata all’email
        Tessera tessera = elencoTessere.get(email);
        if (tessera == null) {
            System.out.println("Errore: nessuna tessera trovata per l’email " + email);
            return false;
        }

        // Effettua la ricarica direttamente (by Information Expert)
        double creditoPrecedente = tessera.getCredito();
        double nuovoCredito = creditoPrecedente + valore;
        tessera.setCredito(nuovoCredito);

        // Conferma la ricarica
        System.out.println(" Ricarica effettuata con successo!");
        System.out.println("Email: " + email);
        System.out.println("Valore ricaricato: " + valore + "$");
        System.out.println("Credito precedente: " + creditoPrecedente + "$");
        System.out.println("Nuovo credito: " + nuovoCredito + "$");

        return true;
    }
}

