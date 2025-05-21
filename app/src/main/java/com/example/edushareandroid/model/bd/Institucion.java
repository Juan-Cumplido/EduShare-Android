package com.example.edushareandroid.model.bd;

public class Institucion {
    public Institucion(int idInstitucion, String nombreInstitucion, String carrera, String nivelEducativo) {
        this.idInstitucion = idInstitucion;
        this.nombreInstitucion = nombreInstitucion;
        this.carrera = carrera;
        this.nivelEducativo = nivelEducativo;
    }

    public int getIdInstitucion() {
        return idInstitucion;
    }

    public void setIdInstitucion(int idInstitucion) {
        this.idInstitucion = idInstitucion;
    }

    public String getNombreInstitucion() {
        return nombreInstitucion;
    }

    public void setNombreInstitucion(String nombreInstitucion) {
        this.nombreInstitucion = nombreInstitucion;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public String getNivelEducativo() {
        return nivelEducativo;
    }

    public void setNivelEducativo(String nivelEducativo) {
        this.nivelEducativo = nivelEducativo;
    }

    private int idInstitucion;
    private String nombreInstitucion;
    private String carrera;
    private String nivelEducativo;

}
