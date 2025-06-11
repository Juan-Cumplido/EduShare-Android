package com.example.edushareandroid.model.base_de_datos;

import androidx.annotation.NonNull;

public class Institucion {
    private int idInstitucion;
    private String nombreInstitucion;
    private String nivelEducativo;

    // Getters y setters
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

    public String getNivelEducativo() {
        return nivelEducativo;
    }

    public void setNivelEducativo(String nivelEducativo) {
        this.nivelEducativo = nivelEducativo;
    }

    @NonNull
    @Override
    public String toString() {
        return nombreInstitucion;
    }

}