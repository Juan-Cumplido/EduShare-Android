package com.example.edushareandroid.ui.verarchivo.comentarios;


import com.google.gson.annotations.SerializedName;

public class RespuestaBase {
    @SerializedName("estado")
    private int estado;

    @SerializedName("mensaje")
    private String mensaje;

    @SerializedName("error")
    private boolean error;

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

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean isSuccess() {
        return !error && (estado == 201 || estado == 200);
    }
}
