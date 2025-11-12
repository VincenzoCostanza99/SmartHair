import java.util.List;
import java.util.Map;
import java.util.Scanner;
import domain.SmartHair;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SmartHair sistema = SmartHair.getInstance();

        boolean esci = false;

        while (!esci) {
            // Se nessun cliente e amministratore è loggato 
            if (sistema.getClienteCorrente() == null && !sistema.isAdminLoggato()) {
                System.out.println("\n=== BENVENUTO IN SMARTHAIR ===");
                System.out.println("1. Registrazione nuovo cliente");
                System.out.println("2. Accesso cliente esistente");
                System.out.println("3. Accesso amministratore");
                System.out.println("0. Esci");
                System.out.print("Scelta: ");

                String scelta = scanner.nextLine().trim();

                switch (scelta) {
                    case "1":
                        registraCliente(sistema, scanner);
                        break;
                    case "2":
                        accediCliente(sistema, scanner);
                        break;
                    case "3":
                        accediAmministratore(sistema, scanner);
                        break;    
                    case "0":
                        esci = true;
                        System.out.println("Arrivederci!");
                        break;
                    default:
                        System.out.println("Scelta non valida.");
                }
            } else if(sistema.getClienteCorrente()!=null){
                // Cliente loggato → mostra menu operativo
                menuCliente(sistema, scanner);
            }else if(sistema.isAdminLoggato()){
                menuAmministratore(sistema,scanner);
            }
        }

        scanner.close();
    }

    // === Funzione di registrazione ===
    private static void registraCliente(SmartHair sistema, Scanner scanner) {
        System.out.println("\n--- REGISTRAZIONE ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Cognome: ");
        String cognome = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        sistema.nuovoCliente(nome, cognome, email, password);
    }

    // === Funzione di accesso ===
    private static void accediCliente(SmartHair sistema, Scanner scanner) {
        System.out.println("\n--- ACCESSO ---");
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        sistema.accesso(email, password);
    }

    // === Funzione di accesso amministratore ===
    private static void accediAmministratore(SmartHair sistema, Scanner scanner) {
        System.out.println("\n--- ACCESSO AMMINISTRATORE ---");
        System.out.print("Email amministratore: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        sistema.AccessoAdmin(email, password);
    }

    // === Menu dopo il login ===
    private static void menuCliente(SmartHair sistema, Scanner scanner) {
        boolean tornaIndietro = false;

        while (!tornaIndietro && sistema.getClienteCorrente() != null) {
            
            GeneraConsiglioCliente(sistema, scanner);

            System.out.println("\n=== MENU CLIENTE ===");
            System.out.println("1. Visualizza prenotazioni");
            System.out.println("2. Effettua una prenotazione");
            System.out.println("3. Ricarica tessera");
            System.out.println("4. Rimuovi prenotazione");
            System.out.println("5. Visualizza credito tessera");
            System.out.println("6. Logout");
            

            System.out.print("Scelta: ");

            String scelta = scanner.nextLine().trim();

            switch (scelta) {
                case "1":
                    sistema.stampaPrenotazioniCliente();
                    break;

                case "2":
                    System.out.println("\n--- SELEZIONE SERVIZIO ---");
                    System.out.println("Servizi disponibili: taglio (20$), colore (40$), piega (10$)");
                    System.out.print("Inserisci servizio desiderato: ");
                    String servizio = scanner.nextLine().toLowerCase();

                    // Seleziona servizio e ottiene la mappa orari disponibili
                    Map<String, Map<String, List<String>>> orariDisponibili = sistema.selezionaServizio(servizio);
                    if (orariDisponibili == null) {
                        System.out.println("Operazione annullata: controlla credito o servizio non valido.\n");
                        break;
                    }

                    // Scelta del cliente
                    System.out.println("\n--- SELEZIONE ORARIO ---");
                    System.out.print("Inserisci giorno (martedi-sabato): ");
                    String giorno = scanner.nextLine().toLowerCase();
                    System.out.print("Inserisci orario (es: 9, 11, 15, 17): ");
                    String orario = scanner.nextLine();
                    System.out.print("Inserisci nome del parrucchiere (vincenzo/jose): ");
                    String dipendente = scanner.nextLine().toLowerCase();

                    // Rimuove l’orario e conferma la selezione
                    String orarioScelto = sistema.selezionaOrario(giorno, orario, dipendente);
                    if (orarioScelto == null) {
                        System.out.println("Orario non disponibile, riprovare.\n");
                        break;
                    }

                    // Conferma prenotazione
                    System.out.println("\n--- CONFERMA PRENOTAZIONE ---");
                    sistema.confermaOrario(orarioScelto, giorno, dipendente, servizio);
                    break;

                case "3":
                    System.out.print("Importo da ricaricare ($): ");
                    try {
                        double importo = Double.parseDouble(scanner.nextLine());
                        sistema.ricaricaTessera(importo);
                    } catch (NumberFormatException e) {
                        System.out.println("Importo non valido.");
                    }
                    break;

                case "4":
                    // Controlla se il cliente ha prenotazioni
                    if (sistema.getClienteCorrente().getPrenotazioni().isEmpty()) {
                        System.out.println("Nessuna prenotazione trovata per "+ sistema.getClienteCorrente().getNome() + ".");
                        break; // esce dal case, non chiede nessun codice
                    }

                    boolean risultato=sistema.stampaPrenotazioniCliente();
                    if(!risultato){
                        break; // esce dal case, non chiede nessun codice
                    }
                    System.out.print("Inserisci codice prenotazione da rimuovere: ");
                    try {
                        int codice = Integer.parseInt(scanner.nextLine());
                        sistema.cancellaPrenotazione(codice);
                    } catch (NumberFormatException e) {
                        System.out.println("Codice non valido.");
                    }
                    break;

                case "5":
                    sistema.visualizzaCreditoCliente();
                    break;

                case "6":
                    sistema.logout();
                    tornaIndietro = true; // Torna al menu iniziale
                    break;

                default:
                    System.out.println("Scelta non valida.");
            }
        }
    }

    // === Menu amministratore ===
    private static void menuAmministratore(SmartHair sistema, Scanner scanner) {
        boolean tornaIndietro = false;

        while (!tornaIndietro && sistema.isAdminLoggato()) {
            System.out.println("\n=== MENU AMMINISTRATORE ===");
            System.out.println("1. Visualizza turni di pulizia");
            System.out.println("2. Assegna turno di pulizia");
            System.out.println("3. Visualizza elenco parrucchieri");
            System.out.println("4. Logout amministratore");

            System.out.print("Scelta: ");

            String scelta = scanner.nextLine().trim();

            switch (scelta) {
                case "1":
                    sistema.visualizzaTurniPulizia();
                    break;

                case "2":
                    List<String> giorniDisponibili= sistema.recuperaGiorniSenzaPulizia();
                    // Se non ci sono giorni disponibili, esce dal case
                    if (giorniDisponibili == null || giorniDisponibili.isEmpty()) {
                        break;
                    }
                    System.out.print("Inserisci nome del parrucchiere: ");
                    String nomeDip = scanner.nextLine();
                    System.out.print("Inserisci giorno da assegnare: ");
                    String giorno = scanner.nextLine();
                    sistema.inserisciDati(nomeDip, giorno);
                    break;

                case "3":
                    sistema.visualizzaElencoParrucchieri();
                    break;
                
                case "4":
                    sistema.logoutAdmin();
                    tornaIndietro = true;
                    break;

                default:
                    System.out.println("Scelta non valida.");
            }
        }
    }

    private static void GeneraConsiglioCliente(SmartHair sistema, Scanner scanner) {

        // Presenza orari disponibili
        if (!sistema.presenzaOrariDisponibili()) {
            return;
        }

        //Controllo prenotazioni e primo consiglio
        String servizioConsigliato = sistema.controlloPresenzaPrenotazioniEPrimoConsiglio();
        if (servizioConsigliato == null) {
            return;
        }

        //Genera consiglio (chiede conferma all’utente)
        boolean accettato = sistema.generaConsiglio(servizioConsigliato);
        if (!accettato) {
            return;
        }

        //Preleva orari disponibili (stampa mappa con giorni e orari)
        Map<String, Map<String, List<String>>> orariDisp = sistema.prelevaOrariDisponibili();
        if (orariDisp == null) {
            return;
        }

        // Inserimento prenotazione da tastiera
        System.out.println("\n--- INSERIMENTO PRENOTAZIONE ---");
        System.out.print("Inserisci giorno (martedi-sabato): ");
        String giorno = scanner.nextLine().trim().toLowerCase();
        System.out.print("Inserisci orario (es. 9, 11, 15, 17): ");
        String ora = scanner.nextLine().trim();
        System.out.print("Inserisci nome del parrucchiere: ");
        String nomeDip = scanner.nextLine().trim().toLowerCase();

        //Aggiungi prenotazione
        boolean ok = sistema.aggiungiPrenotazione(giorno, ora, nomeDip);
        if (ok)
            System.out.println(" Prenotazione aggiunta correttamente!");
        else
            System.out.println(" Errore durante l'aggiunta della prenotazione.");
    }

}
