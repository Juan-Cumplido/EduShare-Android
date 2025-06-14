package com.example.edushareandroid.model.base_de_datos;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Documento  implements Serializable {
    private String titulo;
    private String subtitulo;
    private String detalles;
    private int imagenId;


    private Bitmap imagenMiniatura;

    public Documento(String titulo, String subtitulo, String detalles, Bitmap imagenMiniatura) {
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.detalles = detalles;
        this.imagenId = imagenId;
        this.imagenMiniatura = imagenMiniatura;
    }
    public Documento(String titulo, String subtitulo, String detalles, int imagenId) {
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.detalles = detalles;
        this.imagenId = imagenId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public String getDetalles() {
        return detalles;
    }

    public int getImagenId() {
        return imagenId;
    }
    public Bitmap getImagenMiniatura() {
        return imagenMiniatura;
    }

    public void setImagenMiniatura(Bitmap imagenMiniatura) {
        this.imagenMiniatura = imagenMiniatura;
    }
}
