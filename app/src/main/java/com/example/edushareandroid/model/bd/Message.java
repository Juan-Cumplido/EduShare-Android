package com.example.edushareandroid.model.bd;

public class Message {
    private String id;
    private String autor;
    private String contenido;
    private String fecha;
    private boolean esPropio;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public boolean isEsPropio() {
        return esPropio;
    }

    public void setEsPropio(boolean esPropio) {
        this.esPropio = esPropio;
    }

    public Message(String id, String autor, String contenido, String fecha, boolean esPropio) {
        this.id = id;
        this.autor = autor;
        this.contenido = contenido;
        this.fecha = fecha;
        this.esPropio = esPropio;
    }


}
