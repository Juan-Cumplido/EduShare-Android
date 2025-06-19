package com.example.edushareandroid.ui.usuarios;

public class DejarSeguirRequest {
    private int idUsuarioSeguido;

    public DejarSeguirRequest(int idUsuarioSeguido) {
        this.idUsuarioSeguido = idUsuarioSeguido;
    }

    public int getIdUsuarioSeguido() {
        return idUsuarioSeguido;
    }
}
