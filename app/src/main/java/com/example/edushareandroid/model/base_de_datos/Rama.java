package com.example.edushareandroid.model.base_de_datos;

public class Rama {
    public int getIdRama() {
        return idRama;
    }

    public void setIdRama(int idRama) {
        this.idRama = idRama;
    }

    public String getNombreRama() {
        return nombreRama;
    }

    public void setNombreRama(String nombreRama) {
        this.nombreRama = nombreRama;
    }

    private int idRama;
    private String nombreRama;
}