package com.example.edushareandroid.model.base_de_datos;

public class VerificacionSeguimientoResponse {
    private boolean error;
    private int estado;
    private String mensaje;
    private boolean siguiendo;

    public boolean isError() {
        return error;
    }

    public int getEstado() {
        return estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public boolean isSiguiendo() {
        return siguiendo;
    }
}
