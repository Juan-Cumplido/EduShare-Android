package com.example.edushareandroid.ui.perfil;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class UsuarioPerfil implements Parcelable {
    private int idUsuarioRegistrado;
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private String fotoPerfil;
    private String nombreUsuario;
    private int idInstitucion;
    private String nombreInstitucion;
    private String nivelEducativo;
    private int numeroSeguidores;
    private int numeroSeguidos;
    private String correo;

    protected UsuarioPerfil(Parcel in) {
        idUsuarioRegistrado = in.readInt();
        nombre = in.readString();
        primerApellido = in.readString();
        segundoApellido = in.readString();
        correo = in.readString();
        nombreUsuario = in.readString();
        fotoPerfil = in.readString();
        idInstitucion = in.readInt();
        nombreInstitucion = in.readString();
        nivelEducativo = in.readString();
        numeroSeguidores = in.readInt();
        numeroSeguidos = in.readInt();
    }

    public static final Creator<UsuarioPerfil> CREATOR = new Creator<UsuarioPerfil>() {
        @Override
        public UsuarioPerfil createFromParcel(Parcel in) {
            return new UsuarioPerfil(in);
        }

        @Override
        public UsuarioPerfil[] newArray(int size) {
            return new UsuarioPerfil[size];
        }
    };

    public int getIdUsuarioRegistrado() {
        return idUsuarioRegistrado;
    }

    public void setIdUsuarioRegistrado(int idUsuarioRegistrado) {
        this.idUsuarioRegistrado = idUsuarioRegistrado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public int getIdInstitucion() {
        return idInstitucion;
    }

    public void setIdInstitucion(int idInstitucion) {
        this.idInstitucion = idInstitucion;
    }

    public String getNombreInstitucion() {
        return nombreInstitucion;
    }

    public void setNombreInstitucion(String nombreInstitucion) {
        this.nombreInstitucion = nombreInstitucion;
    }

    public String getNivelEducativo() {
        return nivelEducativo;
    }

    public void setNivelEducativo(String nivelEducativo) {
        this.nivelEducativo = nivelEducativo;
    }

    public int getNumeroSeguidores() {
        return numeroSeguidores;
    }

    public void setNumeroSeguidores(int numeroSeguidores) {
        this.numeroSeguidores = numeroSeguidores;
    }

    public int getNumeroSeguidos() {
        return numeroSeguidos;
    }

    public void setNumeroSeguidos(int numeroSeguidos) {
        this.numeroSeguidos = numeroSeguidos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(idUsuarioRegistrado);
        dest.writeString(nombre);
        dest.writeString(primerApellido);
        dest.writeString(segundoApellido);
        dest.writeString(correo);
        dest.writeString(nombreUsuario);
        dest.writeString(fotoPerfil);
        dest.writeInt(idInstitucion);
        dest.writeString(nombreInstitucion);
        dest.writeString(nivelEducativo);
        dest.writeInt(numeroSeguidores);
        dest.writeInt(numeroSeguidos);
    }
}
