package com.example.edushareandroid.model.base_de_datos.Login;

public class LoginResponse {
    private boolean error;
    private int estado;
    private String mensaje;
    private String token;
    private UsuarioData datos; // datos del usuario logueado

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
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

    public UsuarioData getDatos() {
        return datos;
    }

    public void setDatos(UsuarioData datos) {
        this.datos = datos;
    }
}
