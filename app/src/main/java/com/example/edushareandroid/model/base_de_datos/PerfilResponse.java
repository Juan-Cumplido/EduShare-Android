package com.example.edushareandroid.model.base_de_datos;


public class PerfilResponse {
    private boolean error;
    private int estado;
    private String mensaje;
    private UsuarioPerfil datos; // Aquí está tu perfil

    public boolean isError() {
        return error;
    }

    public int getEstado() {
        return estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public UsuarioPerfil getDatos() {
        return datos;
    }
}
