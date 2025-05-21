package com.example.edushareandroid.model.bd;

public class UsuarioConInstitucion {
    private Usuario usuario;
    private String nombreInstitucion;

    public UsuarioConInstitucion(Usuario usuario, String nombreInstitucion) {
        this.usuario = usuario;
        this.nombreInstitucion = nombreInstitucion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getNombreInstitucion() {
        return nombreInstitucion;
    }

    public String getNombreCompleto() {
        return usuario.getNombre() + " " + usuario.getPrimerApellido() + " " + usuario.getSegundoApellido();
    }
}
