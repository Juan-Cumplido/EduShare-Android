package com.example.edushareandroid.ui.editarperfil;

public class ActualizarPerfil {
    private String nombre;
    private String correo;
    private String primerApellido;
    private String segundoApellido;
    private String nombreUsuario;
    private int idInstitucion;

    public ActualizarPerfil(String nombre, String correo, String primerApellido, String segundoApellido, String nombreUsuario, int idInstitucion) {
        this.nombre = nombre;
        this.correo = correo;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.nombreUsuario = nombreUsuario;
        this.idInstitucion = idInstitucion;
    }

    public ActualizarPerfil() {

    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPrimerApellido() { return primerApellido; }
    public void setPrimerApellido(String primerApellido) { this.primerApellido = primerApellido; }

    public String getSegundoApellido() { return segundoApellido; }
    public void setSegundoApellido(String segundoApellido) { this.segundoApellido = segundoApellido; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public int getIdInstitucion() { return idInstitucion; }
    public void setIdInstitucion(int idInstitucion) { this.idInstitucion = idInstitucion; }
}
