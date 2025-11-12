package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cliente {
    private final String nome;
    private final String cognome;
    private final String email;

    // Relazioni secondo UML
    private final Tessera tessera; 
    private List<Prenotazione> prenotazioni;

    private Consiglio consiglio;
    private boolean primoConsiglio;

    public Cliente(String nome, String cognome, String email) {
        this.nome = Objects.requireNonNull(nome);
        this.cognome = Objects.requireNonNull(cognome);
        this.email = Objects.requireNonNull(email);

        // Inizializzazioni coerenti con il dominio
        this.tessera = new Tessera(0.0);            // ogni cliente possiede una tessera
        this.prenotazioni = new ArrayList<>();      // inizialmente nessuna prenotazione
        this.consiglio=null; // nessun consiglio generato al momento
        this.primoConsiglio=false;
    }

    // Getter di base
    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public String getEmail() { return email; }

    // === Gestione Tessera ===
    public Tessera getTessera() {
        return tessera;
    }

    // === Gestione Prenotazioni ===
    public List<Prenotazione> getPrenotazioni() {
        return prenotazioni;
    }

    public void aggiungiPrenotazione(Prenotazione prenotazione) {
        if (prenotazione != null) {
            prenotazioni.add(prenotazione);
        }
    }

    public void rimuoviPrenotazione(Prenotazione prenotazione) {
        prenotazioni.remove(prenotazione);
    }

    public void stampaPrenotazioni() {
        if (prenotazioni.isEmpty()) {
            System.out.println("Nessuna prenotazione effettuata da " + nome + ".");
        } else {
            System.out.println("\nPrenotazioni di " + nome + " " + cognome + ":");
            for (Prenotazione p : prenotazioni) {
                System.out.println(" - " + p);
            }
        }
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", email='" + email + '\'' +
                ", credito tessera=" + tessera.getCredito() +
                ", numero prenotazioni=" + prenotazioni.size() +
                '}';
    }

    public Consiglio getConsiglio() {
        return consiglio;
    }

    public void setConsiglio(Consiglio consiglio) {
        this.consiglio = consiglio;
    }

    public boolean isPrimoConsiglio() {
        return primoConsiglio;
    }

    public void setPrimoConsiglio(boolean primoConsiglio) {
        this.primoConsiglio = primoConsiglio;   
    }
}
