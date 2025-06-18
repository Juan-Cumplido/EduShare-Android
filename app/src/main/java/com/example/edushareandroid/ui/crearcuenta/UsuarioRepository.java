package com.example.edushareandroid.ui.crearcuenta;

import android.content.Context;

import com.example.edushareandroid.model.base_de_datos.Institucion;
import com.example.edushareandroid.model.base_de_datos.InstitucionesResponse;
import com.example.edushareandroid.model.base_de_datos.UsuarioRegistro;
import com.example.edushareandroid.network.api.ApiResponse;
import com.example.edushareandroid.network.api.ApiService;
import com.example.edushareandroid.network.api.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuarioRepository {
    private final ApiService apiService;

    public UsuarioRepository(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
    }

    public void registrarUsuario(UsuarioRegistro usuario, RegistroCallback callback) {
        apiService.registrarUsuario(usuario).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isError()) {
                    callback.onSuccess();
                } else {
                    String mensaje = parseError(response.code());
                    callback.onError(mensaje);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onError("No se pudo conectar con el servidor. Revisa tu conexión a internet.");
            }
        });
    }

    public void cargarInstituciones(String nivelEducativo, InstitucionesCallback callback) {
        apiService.obtenerInstituciones(nivelEducativo).enqueue(new Callback<InstitucionesResponse>() {
            @Override
            public void onResponse(Call<InstitucionesResponse> call, Response<InstitucionesResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    callback.onSuccess(response.body().getDatos());
                } else {
                    callback.onError(parseError(response.code()));
                }
            }

            @Override
            public void onFailure(Call<InstitucionesResponse> call, Throwable t) {
                callback.onError("Error de red: " + t.getMessage());
            }
        });
    }

    private String parseError(int code) {
        switch (code) {
            case 400: return "Hay errores en los campos enviados.";
            case 409: return "Correo o nombre de usuario ya registrado.";
            case 500: return "Error del servidor. Inténtalo más tarde.";
            default: return "Ocurrió un error. Código: " + code;
        }
    }

    public interface RegistroCallback {
        void onSuccess();
        void onError(String mensaje);
    }

    public interface InstitucionesCallback {
        void onSuccess(List<Institucion> instituciones);
        void onError(String mensaje);
    }
}