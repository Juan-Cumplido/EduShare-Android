package com.example.edushareandroid.model.base_de_datos.comentarios;

public class RespuestaConDatos<T> extends RespuestaBase {
    public T getDatos() {
        return datos;
    }

    public void setDatos(T datos) {
        this.datos = datos;
    }

    private T datos;

}
