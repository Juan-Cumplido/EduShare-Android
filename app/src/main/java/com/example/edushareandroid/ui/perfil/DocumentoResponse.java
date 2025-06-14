package com.example.edushareandroid.ui.perfil;

import android.os.Parcel;
import android.os.Parcelable;

public class DocumentoResponse implements Parcelable {

    private int idPublicacion;
    private int idCategoria;
    private String fecha;
    private String resuContenido;
    private String estado;
    private int numeroLiker;
    private String nivelEducativo;
    private int numeroVisualizaciones;
    private int numeroDescargas;
    private int idUsuarioRegistrado;
    private int idMateriaYRama;
    private int idDocumento;
    private String titulo;
    private String ruta;
    private String nombreCompleto;
    protected DocumentoResponse(Parcel in) {
        idPublicacion = in.readInt();
        idCategoria = in.readInt();
        fecha = in.readString();
        resuContenido = in.readString();
        estado = in.readString();
        numeroLiker = in.readInt();
        nivelEducativo = in.readString();
        numeroVisualizaciones = in.readInt();
        numeroDescargas = in.readInt();
        idUsuarioRegistrado = in.readInt();
        idMateriaYRama = in.readInt();
        idDocumento = in.readInt();
        titulo = in.readString();
        ruta = in.readString();
        nombreCompleto = in.readString();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idPublicacion);
        dest.writeInt(idCategoria);
        dest.writeString(fecha);
        dest.writeString(resuContenido);
        dest.writeString(estado);
        dest.writeInt(numeroLiker);
        dest.writeString(nivelEducativo);
        dest.writeInt(numeroVisualizaciones);
        dest.writeInt(numeroDescargas);
        dest.writeInt(idUsuarioRegistrado);
        dest.writeInt(idMateriaYRama);
        dest.writeInt(idDocumento);
        dest.writeString(titulo);
        dest.writeString(ruta);
        dest.writeString(nombreCompleto);
    }
    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DocumentoResponse> CREATOR = new Creator<DocumentoResponse>() {
        @Override
        public DocumentoResponse createFromParcel(Parcel in) {
            return new DocumentoResponse(in);
        }

        @Override
        public DocumentoResponse[] newArray(int size) {
            return new DocumentoResponse[size];
        }
    };

    public int getIdPublicacion() {
        return idPublicacion;
    }

    public void setIdPublicacion(int idPublicacion) {
        this.idPublicacion = idPublicacion;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getResuContenido() {
        return resuContenido;
    }

    public void setResuContenido(String resuContenido) {
        this.resuContenido = resuContenido;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getNumeroLiker() {
        return numeroLiker;
    }

    public void setNumeroLiker(int numeroLiker) {
        this.numeroLiker = numeroLiker;
    }

    public String getNivelEducativo() {
        return nivelEducativo;
    }

    public void setNivelEducativo(String nivelEducativo) {
        this.nivelEducativo = nivelEducativo;
    }

    public int getNumeroVisualizaciones() {
        return numeroVisualizaciones;
    }

    public void setNumeroVisualizaciones(int numeroVisualizaciones) {
        this.numeroVisualizaciones = numeroVisualizaciones;
    }

    public int getNumeroDescargas() {
        return numeroDescargas;
    }

    public void setNumeroDescargas(int numeroDescargas) {
        this.numeroDescargas = numeroDescargas;
    }

    public int getIdUsuarioRegistrado() {
        return idUsuarioRegistrado;
    }

    public void setIdUsuarioRegistrado(int idUsuarioRegistrado) {
        this.idUsuarioRegistrado = idUsuarioRegistrado;
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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
    public DocumentoResponse() {
    }

}
