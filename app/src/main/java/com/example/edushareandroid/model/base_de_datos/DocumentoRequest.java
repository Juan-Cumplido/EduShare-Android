package com.example.edushareandroid.model.base_de_datos;

public class DocumentoRequest {
    public DocumentoRequest() {

    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    private String titulo;
    private String ruta;

    public DocumentoRequest(String titulo, String ruta) {
        this.titulo = titulo;
        this.ruta = ruta;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getRuta() {
        return ruta;
    }
}
