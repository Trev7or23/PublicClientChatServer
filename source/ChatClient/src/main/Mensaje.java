package main;

public class Mensaje {
    private String contenido;

    public Mensaje() {
    }

    public Mensaje(String contenido) {
        this.contenido = contenido;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}
