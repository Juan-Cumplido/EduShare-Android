package com.example.edushareandroid.model.base_de_datos;

public class PublicacionResponse {
    private boolean error;
    private int estado;
    private String mensaje;
    private int id;

    public boolean isError() {
        return error;
    }

    public int getEstado() {
        return estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public int getId() {
        return id;
    }
}
