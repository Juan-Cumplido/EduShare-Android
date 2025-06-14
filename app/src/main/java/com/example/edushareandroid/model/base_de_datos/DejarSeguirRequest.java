package com.example.edushareandroid.model.base_de_datos;

public class DejarSeguirRequest {
    private int idUsuarioSeguido;

    public DejarSeguirRequest(int idUsuarioSeguido) {
        this.idUsuarioSeguido = idUsuarioSeguido;
    }

    public int getIdUsuarioSeguido() {
        return idUsuarioSeguido;
    }
}
