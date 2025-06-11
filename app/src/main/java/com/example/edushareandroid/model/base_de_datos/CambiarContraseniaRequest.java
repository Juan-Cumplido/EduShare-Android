package com.example.edushareandroid.model.base_de_datos;

import com.google.gson.annotations.SerializedName;

public class CambiarContraseniaRequest {
    private String correo;
    private String codigo;
    @SerializedName("nuevaContrasenia")
    private String nuevaContrasenia;

    public CambiarContraseniaRequest(String correo, String codigo, String nuevaContrasenia) {
        this.correo = correo;
        this.codigo = codigo;
        this.nuevaContrasenia = nuevaContrasenia;
    }

    public String getCorreo() {
        return correo;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNuevaContrasenia() {
        return nuevaContrasenia;
    }
}
