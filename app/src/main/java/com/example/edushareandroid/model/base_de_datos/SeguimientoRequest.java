package com.example.edushareandroid.model.base_de_datos;

public class SeguimientoRequest {
    private int idUsuarioSeguido;

    public SeguimientoRequest(int idUsuarioSeguido) {
        this.idUsuarioSeguido = idUsuarioSeguido;
    }

    public int getIdUsuarioSeguido() {
        return idUsuarioSeguido;
    }

    public void setIdUsuarioSeguido(int idUsuarioSeguido) {
        this.idUsuarioSeguido = idUsuarioSeguido;
    }
}
