package com.example.edushareandroid.model.home.bd;

public class Usuario {
    public Usuario(int idUsuario, String nombre, String primerApellido, String segundoApellido, int fotoRuta, int idAcceso, int idInstitucion) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.fotoRuta = fotoRuta;
        this.idAcceso = idAcceso;
        this.idInstitucion = idInstitucion;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public int getFotoRuta() {
        return fotoRuta;
    }

    public void setFotoRuta(int fotoRuta) {
        this.fotoRuta = fotoRuta;
    }

    public int getIdAcceso() {
        return idAcceso;
    }

    public void setIdAcceso(int idAcceso) {
        this.idAcceso = idAcceso;
    }

    public int getIdInstitucion() {
        return idInstitucion;
    }

    public void setIdInstitucion(int idInstitucion) {
        this.idInstitucion = idInstitucion;
    }

    private int idUsuario;
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private int fotoRuta;
    private int idAcceso;
    private int idInstitucion;


}
