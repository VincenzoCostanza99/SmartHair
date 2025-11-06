package domain;

import java.util.Objects;

public class Cliente {
    private final String nome;
    private final String cognome;
    private final String email;

    public Cliente(String nome, String cognome, String email) {
        this.nome = Objects.requireNonNull(nome);
        this.cognome = Objects.requireNonNull(cognome);
        this.email = Objects.requireNonNull(email);
    }

    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public String getEmail() { return email; }

    @Override
    public String toString() {
        return "Cliente{" + nome + " " + cognome + ", " + email + "}";
    }
}
