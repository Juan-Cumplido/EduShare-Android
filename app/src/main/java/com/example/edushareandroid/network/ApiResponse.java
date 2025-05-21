package com.example.edushareandroid.network;

public class ApiResponse {
    public boolean isError() {
        return error;
    }


    public void setError(boolean error) {
        this.error = error;
    }

    public ApiResponse(boolean error, String mensaje) {
        this.error = error;
        this.mensaje = mensaje;
    }
    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    public String getErrorDetalle() {
        return errorDetalle;
    }

    public void setErrorDetalle(String errorDetalle) {
        this.errorDetalle = errorDetalle;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "error=" + error +
                ", estado=" + estado +
                ", mensaje='" + mensaje + '\'' +
                ", errorDetalle='" + errorDetalle + '\'' +
                '}';
    }

    private boolean error;
    private int estado;
    private String mensaje;
    private String errorDetalle; // Nuevo campo para detalles técnicos


}
