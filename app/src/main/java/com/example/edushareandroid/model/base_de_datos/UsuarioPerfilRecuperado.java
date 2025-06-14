package com.example.edushareandroid.model.base_de_datos;

public class UsuarioPerfilRecuperado {
    private int idUsuarioRegistrado;
    private String nombre;
    private String nombreUsuario;
    private String primerApellido;
    private String segundoApellido;
    private String fotoPerfil;
    private int numeroPublicaciones;
    private int numeroSeguidores;
    private String nivelEducativo;

    // Getters y setters
    public int getIdUsuarioRegistrado() { return idUsuarioRegistrado; }
    public void setIdUsuarioRegistrado(int idUsuarioRegistrado) { this.idUsuarioRegistrado = idUsuarioRegistrado; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getPrimerApellido() { return primerApellido; }
    public void setPrimerApellido(String primerApellido) { this.primerApellido = primerApellido; }

    public String getSegundoApellido() { return segundoApellido; }
    public void setSegundoApellido(String segundoApellido) { this.segundoApellido = segundoApellido; }

    public String getFotoPerfil() { return fotoPerfil; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }

    public int getNumeroPublicaciones() { return numeroPublicaciones; }
    public void setNumeroPublicaciones(int numeroPublicaciones) { this.numeroPublicaciones = numeroPublicaciones; }

    public int getNumeroSeguidores() { return numeroSeguidores; }
    public void setNumeroSeguidores(int numeroSeguidores) { this.numeroSeguidores = numeroSeguidores; }

    public String getNivelEducativo() { return nivelEducativo; }
    public void setNivelEducativo(String nivelEducativo) { this.nivelEducativo = nivelEducativo; }
}
