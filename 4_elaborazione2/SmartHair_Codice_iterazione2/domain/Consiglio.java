package domain;

import java.util.Objects;

public class Consiglio {
    private String servizio; // es. "taglio"

    public Consiglio() { }

    public Consiglio(String servizio) {
        this.servizio = Objects.requireNonNull(servizio);
    }

    public String getServizio() { return servizio; }

    public void setServizio(String servizio) {
        this.servizio = Objects.requireNonNull(servizio);
    }

    @Override
    public String toString() {
        return "Consiglio{servizio='" + servizio + "'}";
    }
}
