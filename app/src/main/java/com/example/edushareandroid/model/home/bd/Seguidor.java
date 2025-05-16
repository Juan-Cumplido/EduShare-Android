package com.example.edushareandroid.model.home.bd;

public class Seguidor {
    public Seguidor(int idSeguidor, int idUsuarioSeguidor, int idUsuarioSeguido) {
        this.idSeguidor = idSeguidor;
        this.idUsuarioSeguidor = idUsuarioSeguidor;
        this.idUsuarioSeguido = idUsuarioSeguido;
    }

    public int getIdSeguidor() {
        return idSeguidor;
    }

    public void setIdSeguidor(int idSeguidor) {
        this.idSeguidor = idSeguidor;
    }

    public int getIdUsuarioSeguidor() {
        return idUsuarioSeguidor;
    }

    public void setIdUsuarioSeguidor(int idUsuarioSeguidor) {
        this.idUsuarioSeguidor = idUsuarioSeguidor;
    }

    public int getIdUsuarioSeguido() {
        return idUsuarioSeguido;
    }

    public void setIdUsuarioSeguido(int idUsuarioSeguido) {
        this.idUsuarioSeguido = idUsuarioSeguido;
    }

    private int idSeguidor;
    private int idUsuarioSeguidor;
    private int idUsuarioSeguido;

    // Getters y Setters
}
