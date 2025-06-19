package com.example.edushareandroid.ui.usuarios;

import java.util.List;

public class PerfilResponseList {
    private boolean error;
    private int estado;
    private String mensaje;
    private List<UsuarioPerfilRecuperado> datos;

    // Getters y setters
    public boolean isError() { return error; }
    public void setError(boolean error) { this.error = error; }

    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public List<UsuarioPerfilRecuperado> getDatos() { return datos; }
    public void setDatos(List<UsuarioPerfilRecuperado> datos) { this.datos = datos; }
}
