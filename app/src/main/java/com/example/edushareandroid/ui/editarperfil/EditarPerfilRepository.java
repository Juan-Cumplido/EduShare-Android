package com.example.edushareandroid.ui.editarperfil;

import android.content.Context;
import android.util.Log;

import com.example.edushareandroid.model.base_de_datos.InstitucionesResponse;
import com.example.edushareandroid.model.base_de_datos.Institucion;
import com.example.edushareandroid.network.api.ApiResponse;
import com.example.edushareandroid.network.api.ApiService;
import com.example.edushareandroid.network.api.RetrofitClient;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarPerfilRepository {
    private final ApiService apiService;

    public EditarPerfilRepository(Context context){
        this.apiService = RetrofitClient.getApiService(context);
    }
    public void obtenerInstituciones(String nivelEducativo, InstitucionesCallback callback) {
        apiService.obtenerInstituciones(nivelEducativo).enqueue(new Callback<InstitucionesResponse>() {
            @Override
            public void onResponse(Call<InstitucionesResponse> call, Response<InstitucionesResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    callback.onSuccess(response.body().getDatos());
                } else {
                    callback.onError("Error al obtener instituciones: código " + response.code());
                }
            }

            @Override
            public void onFailure(Call<InstitucionesResponse> call, Throwable t) {
                callback.onError("Fallo de red: " + t.getMessage());
            }
        });
    }

    public void actualizarPerfil(String token, ActualizarPerfil request, ActualizacionCallback callback) {
        apiService.actualizarPerfil("Bearer " + token, request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getMensaje());
                } else {
                    String mensajeError = "Error al actualizar perfil: código " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            mensajeError += "\n" + response.errorBody().string();
                        } catch (IOException e) {
                            Log.e("EditarPerfil", "Error al leer cuerpo de error", e);
                        }
                    }
                    callback.onError(mensajeError);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onError("Fallo de red: " + t.getMessage());
            }
        });
    }

    public void actualizarAvatar(String token, AvatarRequest avatarRequest, ActualizacionCallback callback) {
        apiService.actualizarAvatar("Bearer " + token, avatarRequest).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getMensaje());
                } else {
                    String mensajeError = "Error al actualizar avatar: código " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            mensajeError += "\n" + response.errorBody().string();
                        } catch (IOException e) {
                            Log.e("EditarPerfil", "Error al leer cuerpo de error", e);
                        }
                    }
                    callback.onError(mensajeError);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onError("Fallo de red: " + t.getMessage());
            }
        });
    }


    public interface InstitucionesCallback {
        void onSuccess(List<Institucion> instituciones);

        void onError(String mensaje);
    }

    public interface ActualizacionCallback {
        void onSuccess(String mensaje);

        void onError(String mensaje);
    }
}
