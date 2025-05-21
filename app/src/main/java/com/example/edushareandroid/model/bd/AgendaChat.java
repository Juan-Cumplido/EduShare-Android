package com.example.edushareandroid.model.bd;

public class AgendaChat {
    private String idAgendaChat;
    private String titulo;
    private String descripcion;
    private String fecha;
    private String hora;
    private String usuario;

    public String getIdAgendaChat() {
        return idAgendaChat;
    }

    public void setIdAgendaChat(String idAgendaChat) {
        this.idAgendaChat = idAgendaChat;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public AgendaChat(String idAgendaChat, String titulo, String descripcion, String fecha, String hora, String usuario) {
        this.idAgendaChat = idAgendaChat;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.hora = hora;
        this.usuario = usuario;
    }

}
