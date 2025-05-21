package com.example.edushareandroid.model.base_de_datos;

public class RecoveryRequest {
    private String correo;

    public RecoveryRequest(String correo) {
        this.correo = correo;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
