package domain;

public class Tessera {
    private double credito;
    private String email;

    public Tessera(String email) {
        this.email = email;
        this.credito = 0;
    }

    // Getter e Setter
    public double getCredito() {
        return credito;
    }

    public void setCredito(double credito) {
        this.credito = credito;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Tessera{email='" + email + "', credito=" + credito + "€}";
    }
}
