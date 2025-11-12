package domain;

import java.util.Objects;

public class Parrucchiere {
    private String nome;

    public Parrucchiere(String nome) {
        this.nome = Objects.requireNonNull(nome, "Il nome del parrucchiere non può essere null");
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return "Parrucchiere{" +
                "nome='" + nome + '\'' +
                '}';
    }
}