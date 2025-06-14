package com.example.edushareandroid.model.base_de_datos;

import com.example.edushareandroid.ui.perfil.DocumentoResponse;

import java.util.List;

public class PublicacionesResponse {
    private boolean error;
    private List<DocumentoResponse> datos;

    public void setDatos(List<DocumentoResponse> datos) {
        this.datos = datos;
    }

    public void setError(boolean error) {
        this.error = error;
    }


    public boolean isError() {
        return error;
    }

    public List<DocumentoResponse> getDatos() {
        return datos;
    }
}
