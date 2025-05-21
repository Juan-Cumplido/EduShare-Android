package com.example.edushareandroid.model.base_de_datos;

public class ChangePasswordRequest {
    private String correo;
    private String codigo;
    private String nuevaContrasenia;

    public ChangePasswordRequest(String correo, String codigo, String nuevaContrasenia) {
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
