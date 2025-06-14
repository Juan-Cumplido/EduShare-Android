package com.example.edushareandroid.model.base_de_datos;

public class DocumentoSubidoResponse {
    private boolean error;
    private int estado;
    private String mensaje;
    private int id;

    public void setError(boolean error) {
        this.error = error;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public void setId(int id) {
        this.id = id;
    }


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
