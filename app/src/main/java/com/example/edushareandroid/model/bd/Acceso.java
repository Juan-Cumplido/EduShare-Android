package com.example.edushareandroid.model.bd;

public class Acceso {
    public Acceso(int idAcceso, String correo, String contrasenia, String nombreUsuario, String estado, String tipoAcceso) {
        this.idAcceso = idAcceso;
        this.correo = correo;
        this.contrasenia = contrasenia;
        this.nombreUsuario = nombreUsuario;
        this.estado = estado;
        this.tipoAcceso = tipoAcceso;
    }

    public int getIdAcceso() {
        return idAcceso;
    }

    public void setIdAcceso(int idAcceso) {
        this.idAcceso = idAcceso;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipoAcceso() {
        return tipoAcceso;
    }

    public void setTipoAcceso(String tipoAcceso) {
        this.tipoAcceso = tipoAcceso;
    }

    private int idAcceso;
    private String correo;
    private String contrasenia;
    private String nombreUsuario;
    private String estado;
    private String tipoAcceso;

    // Getters y Setters
}
