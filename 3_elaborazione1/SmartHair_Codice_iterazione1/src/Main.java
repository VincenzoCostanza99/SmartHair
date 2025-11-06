import java.util.List;
import java.util.Map;
import java.util.Scanner;
import domain.Cliente;
import domain.Prenotazione;
import domain.SmartHair;
import domain.Tessera;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        SmartHair sm = SmartHair.getInstance();

        System.out.println("=== SISTEMA SMART HAIR ===\n");

        boolean uscita = false; // serve per sapere se l'utente vuole uscire del tutto

        while (!uscita) {
            Cliente nuovo = null;
            String emailAttiva = null; // memorizza l'email del cliente loggato
            
            //REGISTRAZIONE OBBLIGATORIA
            while (nuovo == null) {
                System.out.println("--- REGISTRAZIONE ---");
                System.out.print("Nome: ");
                String nome = input.nextLine();
                System.out.print("Cognome: ");
                String cognome = input.nextLine();
                System.out.print("Email: ");
                String email = input.nextLine();
                System.out.print("Password: ");
                String password = input.nextLine();

                nuovo = sm.nuovoCliente(nome, cognome, email, password);
                if (nuovo == null) {
                    System.out.println("\n Registrazione fallita. Riprova.\n");
                }
            }    
        

            System.out.println("\n Registrazione completata con successo! Ora effettua il login.\n");

        
            //ACCESSO OBBLIGATORIO
            boolean loggedIn = false;
            while (!loggedIn) {
                System.out.println("--- ACCESSO ---");
                System.out.print("Email: ");
                String mail = input.nextLine();
                System.out.print("Password: ");
                String pass = input.nextLine();

                boolean success = sm.accesso(mail, pass);
                if (success) {
                    loggedIn = true;
                    emailAttiva = mail;
                    System.out.println("Accesso riuscito! Benvenuto " + mail);
                } else {
                    System.out.println("Credenziali errate. Riprova.\n");
                }
            }

            // MENU SERVIZI
            int scelta = -1;
            while (loggedIn && scelta != 0) {
                System.out.println("\n--- MENU SERVIZI ---");
                System.out.println("1) Effettua Prenotazione");
                System.out.println("2) Visualizza le prenotazioni");
                System.out.println("3) Rimuovi prenotazione");
                System.out.println("4) Logout");
                System.out.println("5) Ricarica Tessera");
                System.out.println("0) Esci dal programma");
                System.out.print("Scelta: ");
                scelta = input.nextInt();
                input.nextLine(); // pulizia buffer

                switch (scelta) {
                    case 1:
                        System.out.println("\n--- SELEZIONE SERVIZIO ---");
                        System.out.println("Servizi disponibili: taglio (20$), colore (40$), piega (10$)");
                        System.out.print("Inserisci servizio desiderato: ");
                        String servizio = input.nextLine().toLowerCase();

                        //Seleziona servizio e ottiene la mappa orari disponibili
                        Map<String, Map<String, List<String>>> orariDisponibili = sm.selezionaServizio(emailAttiva, servizio);
                        if (orariDisponibili == null) {
                            System.out.println(" Operazione annullata: controlla credito o servizio non valido.\n");
                            break;
                        }

                        // Scelta del cliente
                        System.out.println("\n--- SELEZIONE ORARIO ---");
                        System.out.print("Inserisci giorno (martedi-sabato): ");
                        String giorno = input.nextLine().toLowerCase();
                        System.out.print("Inserisci orario (es: 9, 11, 15, 17): ");
                        String orario = input.nextLine();
                        System.out.print("Inserisci nome del parrucchiere (vincenzo/jose): ");
                        String dipendente = input.nextLine().toLowerCase();

                        // Rimuove l’orario e conferma la selezione
                        String orarioScelto = sm.selezionaOrario(giorno, orario, dipendente);
                        if (orarioScelto == null) {
                            System.out.println(" Orario non disponibile, riprovare.\n");
                            break;
                        }

                        // Conferma prenotazione
                        System.out.println("\n--- CONFERMA PRENOTAZIONE ---");
                        sm.confermaOrario(orarioScelto, giorno, dipendente, emailAttiva, servizio);
                        break;

                    case 2:
                        sm.stampaPrenotazioniCliente(emailAttiva);
                        break;
                    
                    case 3:
                        List<Prenotazione> mie = sm.getPrenotazioniCliente(emailAttiva);
                        if (mie.isEmpty()) {
                            System.out.println("Nessuna prenotazione da cancellare.");
                            break;
                        }
                        sm.stampaPrenotazioniCliente(emailAttiva);
                        System.out.print("Inserisci codice da cancellare: ");
                        int codice = input.nextInt();
                        input.nextLine();
                        sm.cancellaPrenotazione(codice, emailAttiva);    
                        break;

                    case 4:
                        System.out.println("\nLogout effettuato. Tornerai alla schermata di registrazione.\n");
                        loggedIn = false;
                        emailAttiva = null;
                        break;

                    case 5:
                        System.out.println("\n--- RICARICA TESSERA ---");
                        System.out.print("Inserisci importo da ricaricare: ");
                        double importo = input.nextDouble();
                        input.nextLine(); // pulizia buffer
                        sm.ricaricaTessera(importo, emailAttiva);
                        break;
                    
                    case 0:
                        System.out.println("Chiusura del sistema...");
                        loggedIn = false; // esce dal ciclo del menu
                        uscita = true;// esce anche dal ciclo principale → termina programma
                        break;

                    default:
                        System.out.println("Scelta non valida!");
                        break;
                }
            }
        }

        input.close();
        System.out.println("\n=== FINE ESECUZIONE ===");
    }
}
