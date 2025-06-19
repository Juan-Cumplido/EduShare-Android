package com.example.edushareandroid.ui.usuarios;

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
