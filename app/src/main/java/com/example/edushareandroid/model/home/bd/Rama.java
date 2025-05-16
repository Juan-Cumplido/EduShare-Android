package com.example.edushareandroid.model.home.bd;

public class Rama {
    public Rama(int idRama, String nombreRama) {
        this.idRama = idRama;
        this.nombreRama = nombreRama;
    }

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

    // Getters y Setters
}
