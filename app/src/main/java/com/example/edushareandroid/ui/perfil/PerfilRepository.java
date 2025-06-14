package com.example.edushareandroid.ui.perfil;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.edushareandroid.model.base_de_datos.PublicacionesResponse;
import com.example.edushareandroid.network.api.ApiResponse;
import com.example.edushareandroid.network.api.RetrofitClient;
import com.example.edushareandroid.network.api.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilRepository {
    private final ApiService usuarioApi;

    public PerfilRepository() {
        usuarioApi = RetrofitClient.getApiService();
    }

    public LiveData<UsuarioPerfil> obtenerPerfil(String token) {
        MutableLiveData<UsuarioPerfil> perfilLiveData = new MutableLiveData<>();

        usuarioApi.obtenerPerfilPropio("Bearer " + token).enqueue(new Callback<PerfilResponse>() {
            @Override
            public void onResponse(Call<PerfilResponse> call, Response<PerfilResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isError()) {
                    perfilLiveData.setValue(response.body().getDatos());
                } else {
                    Log.e("Perfil", "Error al obtener perfil: " + response.code());
                    perfilLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<PerfilResponse> call, Throwable t) {
                Log.e("Perfil", "Fallo en la solicitud", t);
                perfilLiveData.setValue(null);
            }
        });

        return perfilLiveData;
    }

    public LiveData<List<DocumentoResponse>> obtenerPublicacionesUsuario(String token) {
        MutableLiveData<List<DocumentoResponse>> publicacionesLiveData = new MutableLiveData<>();

        usuarioApi.obtenerPublicacionesPropias("Bearer " + token)
                .enqueue(new Callback<PublicacionesResponse>() {
                    @Override
                    public void onResponse(Call<PublicacionesResponse> call, Response<PublicacionesResponse> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isError()) {
                            publicacionesLiveData.setValue(response.body().getDatos());
                            Log.d("Publicaciones", "Recuperadas: " + response.body().getDatos().size());
                        } else {
                            Log.e("Publicaciones", "Error en la respuesta: " + response.code());
                            publicacionesLiveData.setValue(new ArrayList<>());
                        }
                    }

                    @Override
                    public void onFailure(Call<PublicacionesResponse> call, Throwable t) {
                        Log.e("Publicaciones", "Fallo en la solicitud", t);
                        publicacionesLiveData.setValue(new ArrayList<>());
                    }
                });

        return publicacionesLiveData;
    }

    // NUEVO MÉTODO PARA ELIMINAR PUBLICACIÓN
    public LiveData<ResultadoEliminacion> eliminarPublicacion(int idPublicacion, String token) {
        MutableLiveData<ResultadoEliminacion> resultadoLiveData = new MutableLiveData<>();

        usuarioApi.eliminarPublicacion(idPublicacion, "Bearer " + token)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse apiResponse = response.body();
                            if (!apiResponse.isError()) {
                                // Eliminación exitosa
                                resultadoLiveData.setValue(new ResultadoEliminacion(true, "Publicación eliminada exitosamente", idPublicacion));
                                Log.d("EliminarPublicacion", "Publicación eliminada exitosamente");
                            } else {
                                // Error devuelto por la API
                                resultadoLiveData.setValue(new ResultadoEliminacion(false, apiResponse.getMensaje(), idPublicacion));
                                Log.e("EliminarPublicacion", "Error de la API: " + apiResponse.getMensaje());
                            }
                        } else {
                            // Error de respuesta HTTP
                            String mensaje = "Error al eliminar la publicación. Código: " + response.code();
                            resultadoLiveData.setValue(new ResultadoEliminacion(false, mensaje, idPublicacion));
                            Log.e("EliminarPublicacion", mensaje);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        String mensaje = "Error de conexión: " + t.getMessage();
                        resultadoLiveData.setValue(new ResultadoEliminacion(false, mensaje, idPublicacion));
                        Log.e("EliminarPublicacion", "Fallo en la solicitud", t);
                    }
                });

        return resultadoLiveData;
    }

    // Clase para encapsular el resultado de la eliminación
    public static class ResultadoEliminacion {
        private final boolean exitoso;
        private final String mensaje;
        private final int idPublicacionEliminada;

        public ResultadoEliminacion(boolean exitoso, String mensaje, int idPublicacionEliminada) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
            this.idPublicacionEliminada = idPublicacionEliminada;
        }

        public boolean isExitoso() {
            return exitoso;
        }

        public String getMensaje() {
            return mensaje;
        }

        public int getIdPublicacionEliminada() {
            return idPublicacionEliminada;
        }
    }
}