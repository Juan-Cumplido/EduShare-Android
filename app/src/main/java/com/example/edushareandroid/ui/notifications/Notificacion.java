package com.example.edushareandroid.ui.notifications;


public class Notificacion {
    private String titulo;
    private String mensaje;
    private String fecha;

    public Notificacion(String titulo, String mensaje, String fecha) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.fecha = fecha;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getFecha() {
        return fecha;
    }
}
