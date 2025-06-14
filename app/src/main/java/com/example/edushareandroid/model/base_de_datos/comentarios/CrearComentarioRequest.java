package com.example.edushareandroid.model.base_de_datos.comentarios;

public class CrearComentarioRequest {
    private String contenido;
    private int idPublicacion;
    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public int getIdPublicacion() {
        return idPublicacion;
    }

    public void setIdPublicacion(int idPublicacion) {
        this.idPublicacion = idPublicacion;
    }


    public CrearComentarioRequest(String contenido, int idPublicacion) {
        this.contenido = contenido;
        this.idPublicacion = idPublicacion;
    }
}


