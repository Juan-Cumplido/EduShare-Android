package com.example.edushareandroid.model.base_de_datos;

import java.util.List;

public class InstitucionesResponse {
    private int resultado;
    private String mensaje;
    private List<Institucion> datos;

    public int getResultado() {
        return resultado;
    }

    public void setResultado(int resultado) {
        this.resultado = resultado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public List<Institucion> getDatos() {
        return datos;
    }

    public void setDatos(List<Institucion> datos) {
        this.datos = datos;
    }
}
