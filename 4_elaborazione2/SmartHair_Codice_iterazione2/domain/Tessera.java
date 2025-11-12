package domain;

public class Tessera {
    private double credito;

    public Tessera(double creditoIniziale) {
        this.credito = creditoIniziale;
    }

    public Tessera() {
        this(0.0);
    }

    // === Getter e Setter ===
    public double getCredito() {
        return credito;
    }

    public void setCredito(double credito) {
        this.credito = credito;
    }

    public void aggiornaSaldo(double valore) {
    this.credito += valore;
    }

    @Override
    public String toString() {
        return "Tessera{" +
                "credito=" + credito + "$" +
                '}';
    }
}
