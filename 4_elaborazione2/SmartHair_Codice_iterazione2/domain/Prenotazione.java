package domain;

import java.util.Objects;

public class Prenotazione {

    // === Attributi ===
    private static int contatoreCodici = 1; // variabile di classe per autoincremento

    private int codice;          // identificativo univoco della prenotazione
    private String orario;
    private String giorno;
    private String servizio;
    private double prezzoServizio;
    
    private final Parrucchiere parrucchiere; 
    private final Cliente cliente;

    // === Costruttore ===
    public Prenotazione(String orario, String giorno, String servizio,Cliente cliente, Parrucchiere parrucchiere, double prezzoServizio){
        this.codice = contatoreCodici++;  // assegna e incrementa automaticamente
        this.orario = Objects.requireNonNull(orario, "L'orario non può essere null");
        this.giorno = Objects.requireNonNull(giorno, "Il giorno non può essere null");
        this.servizio = Objects.requireNonNull(servizio, "Il servizio non può essere null");
        this.cliente= Objects.requireNonNull(cliente, "Il cliente non può essere null");
        this.parrucchiere = parrucchiere;
        this.prezzoServizio = prezzoServizio;
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

    public double getPrezzoServizio() {
        return prezzoServizio;
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
