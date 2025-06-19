package com.example.edushareandroid.ui.verarchivo.comentarios;

public class RespuestaConDatos<T> extends RespuestaBase {
    public T getDatos() {
        return datos;
    }

    public void setDatos(T datos) {
        this.datos = datos;
    }

    private T datos;

}
