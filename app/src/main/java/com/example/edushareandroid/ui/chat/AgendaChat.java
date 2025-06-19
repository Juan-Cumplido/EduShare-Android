package com.example.edushareandroid.ui.chat;

public class AgendaChat {
    private String id;
    private String titulo;
    private String descripcion;
    private String fecha;
    private String hora;
    private String usuario;

    public AgendaChat(String id, String titulo, String descripcion, String fecha, String hora, String usuario) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.hora = hora;
        this.usuario = usuario;
    }

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getUsuario() {
        return usuario;
    }
}

