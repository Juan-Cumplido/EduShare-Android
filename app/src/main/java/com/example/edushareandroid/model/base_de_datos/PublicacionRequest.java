package com.example.edushareandroid.model.base_de_datos;

public class PublicacionRequest {
    public PublicacionRequest() {

    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getResuContenido() {
        return resuContenido;
    }

    public void setResuContenido(String resuContenido) {
        this.resuContenido = resuContenido;
    }

    public String getNivelEducativo() {
        return nivelEducativo;
    }

    public void setNivelEducativo(String nivelEducativo) {
        this.nivelEducativo = nivelEducativo;
    }

    public int getIdMateriaYRama() {
        return idMateriaYRama;
    }

    public void setIdMateriaYRama(int idMateriaYRama) {
        this.idMateriaYRama = idMateriaYRama;
    }

    public int getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(int idDocumento) {
        this.idDocumento = idDocumento;
    }

    private int idCategoria;
    private String resuContenido;
    private String nivelEducativo;
    private int idMateriaYRama;
    private int idDocumento;

    public PublicacionRequest(int idCategoria, String resuContenido, String nivelEducativo, int idMateriaYRama, int idDocumento) {
        this.idCategoria = idCategoria;
        this.resuContenido = resuContenido;
        this.nivelEducativo = nivelEducativo;
        this.idMateriaYRama = idMateriaYRama;
        this.idDocumento = idDocumento;
    }


}
