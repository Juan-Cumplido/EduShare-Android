package com.example.edushareandroid.utils;

public class Resource<T> {
    public enum Status { SUCCESS, ERROR, LOADING }

    public Status getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMensaje() {
        return mensaje;
    }

    public int getCodigo() {
        return codigo;
    }

    public final Status status;
    public final T data;
    public final String mensaje;
    public final int codigo;

    private Resource(Status status, T data, String mensaje, int codigo) {
        this.status = status;
        this.data = data;
        this.mensaje = mensaje;
        this.codigo = codigo;
    }

    public static <T> Resource<T> success(T data, int codigo) {
        return new Resource<>(Status.SUCCESS, data, null, codigo);
    }

    public static <T> Resource<T> error(String msg, int codigo) {
        return new Resource<>(Status.ERROR, null, msg, codigo);
    }

    public static <T> Resource<T> loading() {
        return new Resource<>(Status.LOADING, null, null, -1);
    }
}
