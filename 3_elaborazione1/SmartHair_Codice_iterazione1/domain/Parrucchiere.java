package domain;

import java.util.*;

public class Parrucchiere {
    private String nome;
    private Map<String, List<String>> orariDisponibili; // giorno -> lista orari

    public Parrucchiere(String nome) {
        this.nome = nome;
        this.orariDisponibili = new HashMap<>();
    }

    public String getNome() {
        return nome;
    }

    public Map<String, List<String>> getOrariDisponibili() {
        return orariDisponibili;
    }

    public void setOrariDisponibili(Map<String, List<String>> orariDisponibili) {
        this.orariDisponibili = orariDisponibili;
    }

    @Override
    public String toString() {
        return "Parrucchiere{" +
                "nome='" + nome + '\'' +
                ", orariDisponibili=" + orariDisponibili +
                '}';
    }
}