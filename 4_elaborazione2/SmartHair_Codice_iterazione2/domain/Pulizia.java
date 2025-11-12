package domain;

public class Pulizia {
    private final String giorno;
    private final Parrucchiere ParrucchiereAssegnato; // parrucchiere che effettua la pulizia

    public Pulizia(String giorno, Parrucchiere assegnato) {
        this.giorno = giorno;
        this.ParrucchiereAssegnato = assegnato;
    }

    public String getGiorno() { return giorno; }
    public Parrucchiere getAssegnato() { return ParrucchiereAssegnato; }

    @Override
    public String toString() {
        return "Pulizia{giorno='" + giorno + "', assegnato=" + ParrucchiereAssegnato.getNome() + "}";
    }
}
