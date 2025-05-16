package com.example.edushareandroid.model.home.bd;

public class Publicacion {
    public Publicacion(int idPublicacion, String categoria, String fecha, String resuContenido, String estado, int numeroLiker, String nivelEducativo, int numeroVisualizaciones, int numeroDescargas, int idUsuarioRegistrado, int idRama, int idMateria, int idDocumento) {
        this.idPublicacion = idPublicacion;
        this.categoria = categoria;
        this.fecha = fecha;
        this.resuContenido = resuContenido;
        this.estado = estado;
        this.numeroLiker = numeroLiker;
        this.nivelEducativo = nivelEducativo;
        this.numeroVisualizaciones = numeroVisualizaciones;
        this.numeroDescargas = numeroDescargas;
        this.idUsuarioRegistrado = idUsuarioRegistrado;
        this.idRama = idRama;
        this.idMateria = idMateria;
        this.idDocumento = idDocumento;
    }

    public int getIdPublicacion() {
        return idPublicacion;
    }

    public void setIdPublicacion(int idPublicacion) {
        this.idPublicacion = idPublicacion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getResuContenido() {
        return resuContenido;
    }

    public void setResuContenido(String resuContenido) {
        this.resuContenido = resuContenido;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getNumeroLiker() {
        return numeroLiker;
    }

    public void setNumeroLiker(int numeroLiker) {
        this.numeroLiker = numeroLiker;
    }

    public String getNivelEducativo() {
        return nivelEducativo;
    }

    public void setNivelEducativo(String nivelEducativo) {
        this.nivelEducativo = nivelEducativo;
    }

    public int getNumeroVisualizaciones() {
        return numeroVisualizaciones;
    }

    public void setNumeroVisualizaciones(int numeroVisualizaciones) {
        this.numeroVisualizaciones = numeroVisualizaciones;
    }

    public int getNumeroDescargas() {
        return numeroDescargas;
    }

    public void setNumeroDescargas(int numeroDescargas) {
        this.numeroDescargas = numeroDescargas;
    }

    public int getIdUsuarioRegistrado() {
        return idUsuarioRegistrado;
    }

    public void setIdUsuarioRegistrado(int idUsuarioRegistrado) {
        this.idUsuarioRegistrado = idUsuarioRegistrado;
    }

    public int getIdRama() {
        return idRama;
    }

    public void setIdRama(int idRama) {
        this.idRama = idRama;
    }

    public int getIdMateria() {
        return idMateria;
    }

    public void setIdMateria(int idMateria) {
        this.idMateria = idMateria;
    }

    public int getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(int idDocumento) {
        this.idDocumento = idDocumento;
    }

    private int idPublicacion;
    private String categoria;
    private String fecha;
    private String resuContenido;
    private String estado;
    private int numeroLiker;
    private String nivelEducativo;
    private int numeroVisualizaciones;
    private int numeroDescargas;
    private int idUsuarioRegistrado;
    private int idRama;
    private int idMateria;
    private int idDocumento;

    // Getters y Setters
}
