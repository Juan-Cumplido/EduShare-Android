package com.example.edushareandroid.ui.perfil;


public class PerfilResponse {
    private boolean error;
    private int estado;
    private String mensaje;
    private UsuarioPerfil datos;

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
