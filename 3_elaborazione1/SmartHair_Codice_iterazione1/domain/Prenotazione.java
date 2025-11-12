package domain;

public class Prenotazione {

    // === Attributi ===
    private static int contatoreCodici = 1; // variabile di classe per autoincremento

    private int codice;          // identificativo univoco della prenotazione
    private String orario;
    private String giorno;
    private String servizio;

    
    private final Parrucchiere parrucchiere; 
    private final Cliente cliente;

    // === Costruttore ===
    public Prenotazione(String orario, String giorno, String servizio,Cliente cliente, Parrucchiere parrucchiere){
        this.codice = contatoreCodici++;  // assegna e incrementa automaticamente
        this.orario = orario;
        this.giorno = giorno;
        this.servizio = servizio;
        this.cliente=cliente;
        this.parrucchiere = parrucchiere;
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

    public String getServizio() {
        return servizio;
    }

    public Parrucchiere getParrucchiere() {
        return parrucchiere;
    }

    public Cliente getCliente() { 
        return cliente; 
    }

    @Override
    public String toString() {
        return "Prenotazione{" +
                "codice=" + codice +
                ", giorno='" + giorno + '\'' +
                ", orario='" + orario + '\'' +
                ", servizio='" + servizio + '\'' +
                ", cliente='" + cliente.getEmail() + '\'' +
                ", parrucchiere='" + parrucchiere.getNome() + 
                '}';
    }
}
