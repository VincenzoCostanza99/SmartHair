package domain;

public class Prenotazione {

    // === Attributi ===
    private static int contatoreCodici = 1; // variabile di classe per autoincremento

    private int codice;            // identificativo univoco della prenotazione
    private String orario;
    private String giorno;
    private String dipendente;
    private String email;
    private String servizio;

    // === Costruttore ===
    public Prenotazione(String orario, String giorno, String dipendente, String email, String servizio) {
        this.codice = contatoreCodici++;  // assegna e incrementa automaticamente
        this.orario = orario;
        this.giorno = giorno;
        this.dipendente = dipendente;
        this.email = email;
        this.servizio = servizio;
    }

    // === Getter ===
    public int getCodice() {
        return codice;
    }

    public String getOrario() {
        return orario;
    }

    public String getGiorno() {
        return giorno;
    }

    public String getDipendente() {
        return dipendente;
    }

    public String getEmail() {
        return email;
    }

    public String getServizio() {
        return servizio;
    }

    // === toString() per debug ===
    @Override
    public String toString() {
        return "Prenotazione{" +
                "codice=" + codice +
                ", giorno='" + giorno + '\'' +
                ", orario='" + orario + '\'' +
                ", dipendente='" + dipendente + '\'' +
                ", servizio='" + servizio + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

