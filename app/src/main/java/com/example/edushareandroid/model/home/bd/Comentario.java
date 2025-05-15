package com.example.edushareandroid.model.home.bd;

public class Comentario {
    private String nombreUsuario;
    private String fecha;
    private String texto;

    public Comentario(String nombreUsuario, String fecha, String texto) {
        this.nombreUsuario = nombreUsuario;
        this.fecha = fecha;
        this.texto = texto;
    }

    public String getNombreUsuario() { return nombreUsuario; }
    public String getFecha() { return fecha; }
    public String getTexto() { return texto; }
}
