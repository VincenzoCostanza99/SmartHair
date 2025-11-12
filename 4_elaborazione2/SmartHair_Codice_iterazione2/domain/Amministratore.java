package domain;

public class Amministratore {
    private final String username;
    private final String password;

    public Amministratore(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }

    /** Verifica le credenziali. */
    public boolean verificaCredenziali(String user, String pass) {
        return this.username.equals(user) && this.password.equals(pass);
    }

    @Override
    public String toString() {
        return "Amministratore{" + "username='" + username + '\'' + '}';
    }
}

